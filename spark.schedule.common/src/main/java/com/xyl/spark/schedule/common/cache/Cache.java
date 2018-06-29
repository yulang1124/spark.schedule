package com.xyl.spark.schedule.common.cache;

import java.util.List;

/**
 * 缓存服务接口
 * <p>
 * Created by XXX on 2018/1/15.
 */
public interface Cache<T> {

    /**
     * 通过key删除
     *
     * @param key
     */
    void delete(String key);


    /**
     * 添加值
     *
     * @param key
     * @param value
     */
    void set(String key, T value);


    /**
     * 单独设置
     *
     * @param node
     */
    void set(Node<T> node);


    /**
     * 批量设置
     *
     * @param nodeList
     */
    void batchSet(List<Node<T>> nodeList);


    /**
     * 批量获取
     *
     * @param keyList
     * @return
     */
    List<T> batchGet(List<String> keyList);


    /**
     * 获取redis value
     *
     * @param key
     * @return
     */
    T get(String key);


    /**
     * 检查key是否已经存在
     *
     * @param key
     * @return
     */
    boolean exists(String key);
}
