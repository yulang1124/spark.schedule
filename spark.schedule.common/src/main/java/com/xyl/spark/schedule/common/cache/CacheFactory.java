package com.xyl.spark.schedule.common.cache;

/**
 * Cache工厂
 * <p>
 * Created by XXX on 2018/1/15.
 */
public interface CacheFactory {

    /**
     * 创建一个缓存
     *
     * @param cacheKey
     * @param valueClass
     * @param <T>
     * @return
     */
    <T> Cache<T> create(String cacheKey, Class<T> valueClass);

    /**
     * 创建一个带老化时间的缓存
     *
     * @param cacheKey
     * @param valueClass
     * @param expireSeconds
     * @param <T>
     * @return
     */
    <T> Cache<T> create(String cacheKey, Class<T> valueClass, long expireSeconds);

    /**
     * 创建一个序列缓存
     *
     * @param cacheKey
     * @param expireSeconds
     * @return
     */
    SequenceCache createSequence(String cacheKey, long expireSeconds);






}
