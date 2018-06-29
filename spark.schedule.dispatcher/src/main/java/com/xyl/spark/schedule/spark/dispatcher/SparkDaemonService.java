package com.xyl.spark.schedule.spark.dispatcher;

import com.xyl.spark.schedule.common.hadoop.spark.SparkTemplate;
import com.xyl.spark.schedule.common.hadoop.spark.TaskConfig;
import com.xyl.spark.schedule.common.hadoop.yarn.Application;
import com.xyl.spark.schedule.common.hadoop.yarn.ApplicationQuery;
import com.xyl.spark.schedule.common.hadoop.yarn.YarnTemplate;
import com.xyl.spark.schedule.spark.dispatcher.config.jobconfig.JobConfig;
import com.xyl.spark.schedule.spark.dispatcher.config.jobconfig.JobConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by XXX on 2017/8/31.
 */

@Service
public class SparkDaemonService {

    Log log = LogFactory.getLog(getClass());

    @Resource
    private JobConfigService jobConfigService;

    @Resource
    private YarnTemplate yarnTemplate;

    @Resource
    private SparkTemplate sparkTemplate;


    /**
     * Spark 守护线程
     */
    @Scheduled(cron = "0/3 * * * * ?")
    @PostConstruct
    protected void sparkDemo(){

        ApplicationQuery applicationQuery = yarnTemplate.createApplicationQuery();
        JobConfig jobConfig = jobConfigService.getJobConfig();

        if(jobConfig.getTaskConfigMap() != null && jobConfig.getTaskConfigMap().size() > 0){
            for(Map.Entry<String, TaskConfig> entry : jobConfig.getTaskConfigMap().entrySet()){
                TaskConfig taskConfig = entry.getValue();
                String taskName = entry.getKey();
                Application[] apps = applicationQuery.filterByName(taskName).filterByStatus("RUNNING", "ACCEPTED").toArray();
                if(apps.length < 1){
                    log.warn("running application is not exists! application name: " + taskName + " will be startup");
                    sparkTemplate.submit(taskConfig, null);
                }else if(apps.length == 1){
                    log.info("application is in running! id: " + apps[0].getId() + ", name: " + apps[0].getName());
                }else if(apps.length > 1){
                    // 只保留最新的一个任务,其它的任务予以删除
                    for(int i = 1; i < apps.length; i++){
                        String id = apps[i].getId();
                        log.warn("redundancy application id: " + id + ",name: " + taskName + " will be shutdown!");
                        sparkTemplate.kill(id);
                    }
                }
            }
        }

    }

}
