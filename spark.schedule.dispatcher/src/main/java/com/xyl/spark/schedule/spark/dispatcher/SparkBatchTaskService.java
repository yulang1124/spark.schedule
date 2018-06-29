package com.xyl.spark.schedule.spark.dispatcher;

import com.alibaba.fastjson.JSON;
import com.xyl.spark.schedule.common.hadoop.spark.SparkTemplate;
import com.xyl.spark.schedule.common.hadoop.spark.TaskConfig;
import com.xyl.spark.schedule.common.schedule.ScheduleService;
import com.xyl.spark.schedule.spark.dispatcher.config.jobconfig.JobConfig;
import com.xyl.spark.schedule.spark.dispatcher.config.taskconfig.BatchTaskConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by XXX on 2017/8/31.
 */
@Service
public class SparkBatchTaskService implements ConfigChangeListener{


    @Resource
    private ScheduleService scheduleService;

    @Resource
    private SparkTemplate sparkTemplate;

    Log log = LogFactory.getLog(getClass());

    @Override
    public void changed(JobConfig jobConfig) {
        log.info("hstream.xml changed!");

        Map<String,String> jobCronExpressionMap = scheduleService.getAllJobCronExpressionMap();
        if(jobCronExpressionMap == null){
            // 存在异常或者定时任务尚未启动
            log.warn("getAllJobCronExpressionMap is null!");
            return;
        }

        Map<String, BatchTaskConfig> batchTaskConfigMap = jobConfig.getBatchTaskConfigMap();

        for(Map.Entry<String, BatchTaskConfig> entry:batchTaskConfigMap.entrySet()){
            String name = entry.getKey();
            BatchTaskConfig batchTaskConfig = entry.getValue();
            String cronExpression = batchTaskConfig.getCron();
            if(jobCronExpressionMap.containsKey(name)){
                String cron = jobCronExpressionMap.remove(name); //正在运行中的任务cron表达式
                if(!StringUtils.equals(cronExpression, cron)){
                    //周期发生变更，重新注册任务
                    log.info("name: " + name + ", cronExpression changed: " + cron +" -> " + cronExpression +", restart this task!");
                    scheduleService.removeJob(name);
                    scheduleService.addJob(name, cronExpression, new SparkJobRunnable(batchTaskConfig));
                }

            }else{
                // 该任务尚未运行
                log.info("name: " + name + ", cronExpression: " + cronExpression + ", is not exists, start this task!");
                scheduleService.addJob(name, cronExpression, new SparkJobRunnable(batchTaskConfig));
            }
        }
        // jobCronExpressionMap中剩余的任务需要关闭
        for(Map.Entry<String,String> entry : jobCronExpressionMap.entrySet()){
            String name = entry.getKey();
            String cronExpression = entry.getValue();
            log.info("name: " + name + ", cronExpression: " + cronExpression + ", this task is invalid, remove this task!");
            scheduleService.removeJob(name);
        }
    }

    class SparkJobRunnable implements Runnable{
        private TaskConfig taskConfig;
        public SparkJobRunnable(TaskConfig taskConfig){
            this.taskConfig = taskConfig;
        }

        @Override
        public void run() {
            log.info("submit batch task: " + JSON.toJSONString(taskConfig));
            // 需要实现spark submit的功能---  2017／09／27 暂停一下

            sparkTemplate.submit(taskConfig, null);

        }
    }
}


