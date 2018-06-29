package com.xyl.spark.schedule.common.cache;

/**
 * 高并发缓存序列
 *
 * Created by XXX on 2018/1/15.
 */
public interface Sequence {

    /**
     * 获取下一个数字
     *
     * @return
     */
    Long sequence();

    Long sequenceBy(Long number);

    /**
     * 恢复从0计数
     */
    void reset();
}
