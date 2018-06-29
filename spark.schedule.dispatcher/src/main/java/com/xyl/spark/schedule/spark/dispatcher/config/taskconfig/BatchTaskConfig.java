package com.xyl.spark.schedule.spark.dispatcher.config.taskconfig;


import com.xyl.spark.schedule.common.hadoop.spark.TaskConfig;
import com.xyl.spark.schedule.common.xml.annotation.XmlAttribute;

/**
 * Created by XXX on 2017/9/6.
 *
 * Spark离线批处理任务 运行参数
 */
public class BatchTaskConfig extends TaskConfig {

    @XmlAttribute(name = "cronExpression")
    private String cron;

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getCron() {
        return cron;
    }
}
