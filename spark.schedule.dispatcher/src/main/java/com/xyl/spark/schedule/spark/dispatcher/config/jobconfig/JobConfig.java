package com.xyl.spark.schedule.spark.dispatcher.config.jobconfig;

import com.xyl.spark.schedule.common.hadoop.spark.TaskConfig;
import com.xyl.spark.schedule.common.xml.annotation.XmlMap;
import com.xyl.spark.schedule.spark.dispatcher.config.taskconfig.BatchTaskConfig;

import java.util.Map;

/**
 * Created by XXX on 2017/9/6.
 *
 * Spark 任务配置
 */
public class JobConfig {

    @XmlMap(key = "class", valueClass = TaskConfig.class, name = "sparkstreaming")
    private Map<String, TaskConfig> taskConfigMap;

    @XmlMap(key = "class", valueClass = BatchTaskConfig.class, name = "spark")
    private Map<String, BatchTaskConfig> batchTaskConfigMap;

    public void setBatchTaskConfigMap(Map<String, BatchTaskConfig> batchTaskConfigMap) {
        this.batchTaskConfigMap = batchTaskConfigMap;
    }

    public Map<String, TaskConfig> getTaskConfigMap() {
        return taskConfigMap;
    }

    public void setTaskConfigMap(Map<String, TaskConfig> taskConfigMap) {
        this.taskConfigMap = taskConfigMap;
    }

    public Map<String, BatchTaskConfig> getBatchTaskConfigMap() {
        return batchTaskConfigMap;
    }
}
