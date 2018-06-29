package com.xyl.spark.schedule.spark.dispatcher;


import com.xyl.spark.schedule.spark.dispatcher.config.jobconfig.JobConfig;

/**
 * Created by XXX on 2017/9/8.
 */
public interface ConfigChangeListener {
    void changed(JobConfig jobConfig);
}
