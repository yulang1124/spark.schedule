package com.xyl.spark.schedule.common.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by XXX on 2017/9/18.
 */
public class ScheduleJob implements Job {

    Log log = LogFactory.getLog(getClass());

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Runnable job = (Runnable) jobExecutionContext.getMergedJobDataMap().get("runnable");
        if(job != null){
            job.run();
        }else {
            log.error("JobExecutionContext'mergedJobDataMap is null!");
        }
    }
}
