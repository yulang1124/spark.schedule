package com.xyl.spark.schedule.common.hadoop.spark;

import com.xyl.spark.schedule.common.hadoop.yarn.Application;
import com.xyl.spark.schedule.common.hadoop.yarn.ApplicationQuery;
import com.xyl.spark.schedule.common.hadoop.yarn.YarnTemplate;
import com.xyl.spark.schedule.common.utils.CommandService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by XXX on 2017/9/29.
 */

@Service
public class SparkTemplate {

    Log log = LogFactory.getLog(getClass());

    /**
     * 表示运行结束的状态: 被杀掉和自己结束
     */
    private static final Set<String> finishedStateSet = new HashSet<String>(Arrays.asList("KILLED", "FINISHED"));

    @Resource
    private CommandService commandService;

    @Resource
    private YarnTemplate yarnTemplate;

    public void kill(String id){
        String yarnKillCommond = "yarn application -kill " + id;
        commandService.execute(60000L, "/bin/sh", "-c", yarnKillCommond);
    }

    /**
     * 任务提交
     * @param sparkJob
     * @param params 提交jar的main函数的 args 参数
     */
    public void submit(TaskConfig sparkJob, String[] params){
        if(StringUtils.isEmpty(sparkJob.getClassName())){
            throw new IllegalArgumentException("SparkJobConfig class cannot null!");
        }
        if(StringUtils.isEmpty(sparkJob.getJar())){
            throw new IllegalArgumentException("SparkJobConfig jar cannot null!");
        }

        String jobName = sparkJob.getClassName();
        ApplicationQuery applicationQuery = yarnTemplate.createApplicationQuery();
        List<Application> applicationList= applicationQuery.filterByName(jobName).filterByStatus("RUNNING", "ACCEPTED").list();
        if(!applicationList.isEmpty()){
            // 任务中已经有正在运行的任务了,将会首先予以关闭
            for (Application application : applicationList){
                String id = application.getId();
                log.warn("redundancy application id: " + id + ", name: " + jobName + " will ne shutdown!");
                this.kill(id);
            }
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("spark2-submit --class ").append(sparkJob.getClassName());
        if (params != null && params.length > 0)
        {
            buffer.append(" ").append(StringUtils.join(params, " "));
        }
        if (!StringUtils.isEmpty(sparkJob.getLib()))
        {
            buffer.append(" --jars ").append(getJars(sparkJob.getLib()));
        }
        buffer.append(" --master ").append(sparkJob.getMaster())
                .append(" --deploy-mode ").append(sparkJob.getDeployMode())
                .append(" --driver-memory ").append(sparkJob.getDriverMemory())
                .append(" --driver-cores ").append(sparkJob.getDriverCores())
                .append(" --executor-memory ").append(sparkJob.getExecutorMemory())
                .append(" --executor-cores ").append(sparkJob.getExecutorCores())
                .append(" --num-executors ").append(sparkJob.getNumExecutors())
                .append(" ").append(sparkJob.getJar());



        commandService.execute(60000L, "/bin/sh", "-c", buffer.toString());
    }

    /**
     * 加载jar相对路径,或者是某目录下的所有jar相对路径
     *
     * @param libPath
     * @return
     */
    private String getJars(String libPath){
        String[] paths = StringUtils.split(libPath, ",");
        List<String> list = new ArrayList<String>();
        for(String path : paths){
            if(StringUtils.endsWithIgnoreCase(path, ".jar")){
                //是jar包
                list.add(path);
            }else {
                // 是目录
                File dir = new File(path);
                if(dir.exists() && dir.isDirectory()){
                    // 存在并且是目录
                    List<String> jarList = new ArrayList<String>();
                    loadAllJars(dir, path, jarList);
                    list.addAll(jarList);
                    jarList = null;
                }
            }
        }
        return StringUtils.join(list, ",");
    }

    private void loadAllJars(File dir, String parentPath, List<String> jarList){
        if(dir == null || !dir.isDirectory()){
            return;
        }
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isDirectory()){
                loadAllJars(file, parentPath, jarList);
            }else if(file.isFile()){
                String fileName = file.getName();
                if(StringUtils.endsWithIgnoreCase(fileName, ".jar")){
                    jarList.add(parentPath + "/" + fileName);
                }
            }

        }
    }


}
