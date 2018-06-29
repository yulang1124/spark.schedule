package com.xyl.spark.schedule.common.hadoop.yarn;

import com.alibaba.fastjson.JSON;
import com.xyl.spark.schedule.common.httpclient.HttpClientSession;
import com.xyl.spark.schedule.common.httpclient.HttpClientTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by XXX on 2017/9/26.
 */

@Service
public class YarnTemplate {


    @Resource
    private HttpClientTemplate httpClientTemplate;

    @Value("${yarn.app.url}")
//    private String yarnAppsUrl;
    private String yarnAppsUrl = "http://srv-cloudera-node3:8088/ws/v1/cluster/apps";

    Log log = LogFactory.getLog(getClass());

    /**
     * 获取yarn的全部application
     *
     * @return
     */
    public ApplicationQuery createApplicationQuery(){

        log.info("getRunningApplications from yarn REST URL: " + yarnAppsUrl);
        HttpClientSession httpClientSession = httpClientTemplate.createHttps();
        try {
            String response = httpClientSession.get(yarnAppsUrl);
            ApplicationList applicationList = JSON.parseObject(response, ApplicationList.class);
            Apps apps = applicationList.getApps();
            if(apps != null){
                return new ApplicationQuery(apps.getApp());
            }else {
                return new ApplicationQuery();
            }

        } catch (Exception e) {
            log.error("getRunningApplications failed!", e);
            return null;
        }finally {
            IOUtils.closeQuietly(httpClientSession);
        }
    }

    /**
     * 根据指定ID获取Application
     *
     * @param id
     * @return
     */
    public Application getApplicationById(String id){
        HttpClientSession httpClientSession = httpClientTemplate.createHttps();
        try {
            String response = httpClientSession.get(yarnAppsUrl +"/" + id);
            App app = JSON.parseObject(response, App.class);
            if(app != null){
                return app.getApp();
            }
        } catch (Exception e) {
            log.error("getApplicationById failed!", e);
        }finally {
            IOUtils.closeQuietly(httpClientSession);
        }
        return null;
    }


    public String getYarnAppsUrl() {
        return yarnAppsUrl;
    }

    public void setYarnAppsUrl(String yarnAppsUrl) {
        this.yarnAppsUrl = yarnAppsUrl;
    }

    public static void main(String[] args) {
        YarnTemplate yarnTemplate = new YarnTemplate();
        ApplicationQuery aq = yarnTemplate.createApplicationQuery();
        aq = aq.filterByName("com.qiaofang.estatedata.spark.test.StreamWordCount").filterByStatus("FINISHED");
        List<Application> ls = aq.list();
        for(Application a:ls){
            System.out.println(a.getId() +" " + a.getName() +" " + a.getState());
        }

    }
}
