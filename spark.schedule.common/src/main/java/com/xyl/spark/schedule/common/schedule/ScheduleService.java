package com.xyl.spark.schedule.common.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by XXX on 2017/9/11.
 */



public class ScheduleService {

    private static final String JOB_GROUP_NAME = "com.qiaofang";
    private static final String TRIGGER_GROUP_NAME = "com.qiaofang";

    private Scheduler scheduler;

    Log log = LogFactory.getLog(getClass());


    /**
     * 启动周期任务
     */
    public void start() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            if(scheduler.isStarted()){
                log.info("Schedeuler already started!");
            }else {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            log.error("Scheduler start faild!", e);
        }
    }


    /**
     * 关闭周期任务
     */
    public void destory() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            log.error("Scheduler stop failed!", e);
        }
    }

    /**
     * 获取当前所有正在运行的任务的名称和周期表达式的Map<br>
     * 返回null表示尚未启动或者有异常情况<br>
     *
     * @return map<jobName, cron表达式>
     */
    public Map<String, String> getAllJobCronExpressionMap() {

        try {
            if (!scheduler.isStarted()) {
                return null;
            }
            Map<String, String> jobCronExpressionMap = new HashMap<String, String>();
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JOB_GROUP_NAME));
            for(JobKey jobKey:jobKeys){
                String cronExpression = scheduler.getJobDetail(jobKey).getJobDataMap().getString("cronExpression");
                jobCronExpressionMap.put(jobKey.getName(), cronExpression);
            }
            return jobCronExpressionMap;
        } catch (SchedulerException e) {
            log.error("getAllJobCronExpressionMap failed!", e);
            return null;
        }
    }

    /**
     * 获取任务对应的周期表达式
     *
     * @param name
     * @return
     */
    public String getJobCronExpression(String name){
        try {
            JobKey jobKey = JobKey.jobKey(name, JOB_GROUP_NAME);
            String cronStr = scheduler.getJobDetail(jobKey).getJobDataMap().getString("cronExpression");
            return cronStr;
        } catch (SchedulerException e) {
            log.error("getJobDetail failed!", e);
            return null;
        }
    }

    /**
     * 添加一个定时任务
     *
     * @param name
     * @param cronExpression
     * @param runnable
     */
    public void addJob(String name, String cronExpression, Runnable runnable){
        JobDetail job = JobBuilder.newJob(ScheduleJob.class).withIdentity(name, JOB_GROUP_NAME).build();
        job.getJobDataMap().put("runnable", runnable);
        job.getJobDataMap().put("cronExpression", cronExpression);
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, TRIGGER_GROUP_NAME).withSchedule(scheduleBuilder).build();
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("scheduler addJob faield!", e);
        }
    }


    /**
     * 删除一个任务
     *
     * @param name 任务名
     */
    public void removeJob(String name){
        TriggerKey triggerKey = TriggerKey.triggerKey(name, TRIGGER_GROUP_NAME);
        JobKey jobKey = JobKey.jobKey(name, JOB_GROUP_NAME);
        try {
            scheduler.pauseTrigger(triggerKey);  //停止触发器
            scheduler.unscheduleJob(triggerKey); //移除触发器
            scheduler.deleteJob(jobKey); //删除任务
        } catch (SchedulerException e) {
            log.error("scheduler remove job failed!", e);
        }
    }


}
