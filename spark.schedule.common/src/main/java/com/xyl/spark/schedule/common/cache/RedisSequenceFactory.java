package com.xyl.spark.schedule.common.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于Redis的序列号工厂
 *
 * Created by XXX on 2018/1/15.
 */
public class RedisSequenceFactory implements SequenceFactory{

    private static final String DEFAULT_CHARSET = "UTF-8";

    //高并发缓存序列集，每个key对应一个
    private final Map<String, Sequence> sequenceMap = new HashMap<String, Sequence>();

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    Log log = LogFactory.getLog(getClass());


    /**
     * 使用Spring的依赖注入
     *
     * @param redisTemplate
     */
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Sequence create(String key) {
        if(sequenceMap.containsKey(key)){
            throw new RuntimeException("Sequence create duplicated! key: " + key);
        }
        Sequence sequence = new RedisSequence(key);
        sequenceMap.put(key, sequence);
        return sequence;
    }

    //高速并发缓存序列实现
    class RedisSequence implements Sequence{

        private String key;
        private byte[] keyBytes;
        private RedisSequence(String key){
            this.key = key;
            try {
                this.keyBytes = key.getBytes(DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                log.error("UnsupportedEncodingException", e);
            }
        }

        @Override //每次获取序列自增1，进行使用计数
        public Long sequence() {
            return this.sequenceBy(1L);
        }

        @Override
        public Long sequenceBy(final Long number) {
            return redisTemplate.execute(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    return redisConnection.incrBy(keyBytes, number);
                }
            });
        }

        @Override
        public void reset() {
            redisTemplate.delete(key);
        }
    }
}
