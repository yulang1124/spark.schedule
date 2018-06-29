package com.xyl.spark.schedule.common.cache;

/**
 * Created by XXX on 2018/1/15.
 */
public interface SequenceCache extends Cache<Long>{

    Long sequence(String key);

    void reset(String key);

}
