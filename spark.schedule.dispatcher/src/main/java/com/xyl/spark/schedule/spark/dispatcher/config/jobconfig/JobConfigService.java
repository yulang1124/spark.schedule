package com.xyl.spark.schedule.spark.dispatcher.config.jobconfig;

import com.xyl.spark.schedule.common.xml.XmlParseService;
import com.xyl.spark.schedule.spark.dispatcher.ConfigChangeListener;
import com.xyl.spark.schedule.spark.dispatcher.config.jobconfig.JobConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by XXX on 2017/9/6.
 */
@Service
public class JobConfigService {
    @Resource
    private XmlParseService xmlParseService;

    @Resource
    ConfigChangeListener configChangeListener;

    private JobConfig config;

    private long lastModifiedTime = -1L;

    Log log = LogFactory.getLog(getClass());


    /**
     * 获取Spark 任务配置,支持热更新
     *
     * @return
     */
    public JobConfig getJobConfig() {
        File configFile = new File("./conf/hstream.xml");
        long modifiedTime = configFile.lastModified();
        if (modifiedTime - lastModifiedTime > 1000) {
            log.info("start parse configFile " + configFile.getAbsolutePath());
            InputStream in = null;
            try {
                in = new FileInputStream(configFile);
                config = xmlParseService.parseObject(JobConfig.class, in);
                lastModifiedTime = modifiedTime;
                new Thread() {
                    @Override
                    public void run() {
                        configChangeListener.changed(config);
                    }

                }.start();
            } catch (Exception e) {
                log.error("configFile : " + configFile.getAbsolutePath() + " parsed failed!", e);
            } finally {
                IOUtils.closeQuietly(in);
            }

        }

        return config;
    }

    public static void main(String[] args) {
        JobConfigService jobConfigService = new JobConfigService();
        JobConfig jobConfig = jobConfigService.getJobConfig();
        System.out.println(jobConfig.getBatchTaskConfigMap().size() );
    }

}
