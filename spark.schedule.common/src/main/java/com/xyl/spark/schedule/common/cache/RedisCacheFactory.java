package com.xyl.spark.schedule.common.cache;

import com.alibaba.fastjson.JSON;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于Redis的Cache工厂
 *
 * Created by XXX on 2018/1/15.
 */
public class RedisCacheFactory implements CacheFactory{

    private static final NumberFormat numberFormat = new DecimalFormat("0.000000");
    private static final String REDIS_KEY = "%s-%s";
    private static final String DEFAULT_CHARSET = "UTF-8";

    private final Map<String, Cache<?>> cacheMap = new HashMap<String, Cache<?>>();

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    Log log = LogFactory.getLog(getClass());

    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public synchronized  <T> Cache<T> create(String cacheKey, Class<T> valueClass) {
        return this.create(cacheKey, valueClass, 0L);
    }

    @Override
    public <T> Cache<T> create(String cacheKey, Class<T> valueClass, long expireSeconds) {
        if(cacheMap.containsKey(cacheKey)){
            throw new RuntimeException("Cache create duplicated! cacheKey: " + cacheKey);
        }
        Cache<T> cache = new RedisCache<T>(cacheKey, valueClass, expireSeconds);
        cacheMap.put(cacheKey, cache);
        return cache;
    }

    public static String redisKey(String cacheKey, String key){
        return String.format(REDIS_KEY, cacheKey, key);
    }

    @Override
    public SequenceCache createSequence(String cacheKey, long expireSeconds) {
        if(cacheMap.containsKey(cacheKey)){
            throw new RuntimeException("Cache create duplicated! cacheKey: " + cacheKey);
        }
        RedisSequenceCache sequenceCache = new RedisSequenceCache(cacheKey, expireSeconds);
        cacheMap.put(cacheKey, sequenceCache);
        return sequenceCache;
    }


    class RedisSequenceCache extends RedisCache<Long> implements SequenceCache{

        protected RedisSequenceCache(String cacheKey, long expireSeconds) {
            super(cacheKey, Long.class, expireSeconds);
        }

        @Override
        public Long sequence(final String key) {
            return redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection connection) throws DataAccessException {
                    byte[] keyBytes = null;
                    String rowKey = redisKey(cacheKey, key);
                    try {
                        keyBytes = rowKey.getBytes(DEFAULT_CHARSET);
                    } catch (UnsupportedEncodingException e) {
                        log.error("UnsupportedEncodingException", e);
                        return null;
                    }
                    return connection.incrBy(keyBytes, 1L);
                }
            });
        }

        @Override
        public void reset(String key) {
            super.set(key, 0L);

        }
    }


    /**
     * 基于redis的缓存实现
     * @param <T>
     */
    class RedisCache<T> implements Cache<T>{

        protected String cacheKey;
        protected Class<T> valueClass;
        protected long expireSeconds;

        public RedisCache(String cacheKey, Class<T> valueClass, long expireSeconds) {
            this.cacheKey = cacheKey;
            this.valueClass = valueClass;
            this.expireSeconds = expireSeconds;
        }

        @Override
        public void delete(String key) {
            redisTemplate.delete(key);
        }

        @Override
        public void set(final String key, final T value) {
            redisTemplate.execute(new RedisCallback<Void>() {
                @Override
                public Void doInRedis(RedisConnection connection) throws DataAccessException {
                    RedisSerializer<String> stringSerializer =  redisTemplate.getStringSerializer();
                    String rowkey = redisKey(cacheKey, key);
                    byte[] keyBytes = stringSerializer.serialize(rowkey);
                    byte[] valueBytes = stringSerializer.serialize(toSerializeValue(value));
                    connection.set(keyBytes, valueBytes);
                    if(expireSeconds > 0L){
                        connection.expire(keyBytes, expireSeconds);
                    }
                    return null;
                }
            });
        }


        @Override
        public void set(Node<T> node) {
            this.set(node.getKey(), node.getValue());
        }

        @Override
        public void batchSet(final List<Node<T>> nodeList) {
            if(nodeList == null || nodeList.isEmpty()){
                return;
            }
            redisTemplate.executePipelined(new RedisCallback<Object>() {

                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    for(Node<T> node : nodeList){
                        String rowkey = redisKey(cacheKey, node.getKey());
                        byte[] keyBytes = stringSerializer.serialize(rowkey);
                        byte[] valueBytes = stringSerializer.serialize(toSerializeValue(node.getValue()));
                        connection.set(keyBytes, valueBytes);
                        if(expireSeconds > 0L){
                            connection.expire(keyBytes, expireSeconds);
                        }

                    }
                    return null;
                }
            });
        }

        @Override
        public List<T> batchGet(final List<String> keyList) {
            if (keyList == null || keyList.isEmpty())
            {
                return new ArrayList<T>();
            }
            List<Object> valueList = redisTemplate.executePipelined(new RedisCallback<Object>()
            {
                public Object doInRedis(RedisConnection connection) throws DataAccessException
                {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    for (String key : keyList)
                    {
                        String rowKey = redisKey(cacheKey, key);
                        byte[] keyBytes = stringSerializer.serialize(rowKey);
                        byte[] valueBytes = connection.get(keyBytes);
                        if (valueBytes == null || valueBytes.length < 1)
                        {
                            continue;
                        }
                        connection.rPop(valueBytes);
                    }
                    return null;
                }
            });
            List<T> list = new ArrayList<T>();
            for (Object value : valueList)
            {
                if (value instanceof String)
                {
                    T object = parseObject((String) value);
                    list.add(object);
                }
            }
            return list;
        }

        @Override
        public T get(final String key) {
            return redisTemplate.execute(new RedisCallback<T>() {
                @Override
                public T doInRedis(RedisConnection connection) throws DataAccessException {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    String rowKey = redisKey(cacheKey, key);
                    byte[] keyBytes = stringSerializer.serialize(rowKey);
                    byte[] valueBytes = connection.get(keyBytes);
                    if(valueBytes == null || valueBytes.length < 1){
                        return null;
                    }
                    return parseObject(stringSerializer.deserialize(valueBytes));
                }
            });
        }

        @Override
        public boolean exists(String key) {
            return redisTemplate.hasKey(redisKey(cacheKey, key));
        }



        private T parseObject(String serializeValue){
            if(valueClass == String.class){
                return valueClass.cast(serializeValue);
            }
            else if(StringUtils.isEmpty(serializeValue)){
                return null;
            }
            else if(Number.class.isAssignableFrom(valueClass)){
                return valueClass.cast(ConvertUtils.convert(serializeValue, valueClass));
            }
            else {
                return JSON.parseObject(serializeValue, valueClass);
            }
        }

        private String toSerializeValue(T value){
            if(value == null){
                return "";
            }
            else if(valueClass == String.class){
                return (String)value;
            }
            else if (valueClass == Integer.class)
            {
                return Integer.toString((Integer) value);
            }
            else if (valueClass == Long.class)
            {
                return Long.toString((Long) value);
            }
            else if (valueClass == Double.class)
            {
                return numberFormat.format((Double) value);
            }
            else
            {
                return JSON.toJSONString(value);
            }
        }


    }

}
