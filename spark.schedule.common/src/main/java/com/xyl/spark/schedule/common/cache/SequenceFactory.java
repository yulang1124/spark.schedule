package com.xyl.spark.schedule.common.cache;

/**
 * Created by XXX on 2018/1/15.
 */
public interface SequenceFactory {

    Sequence create(String key);

}
