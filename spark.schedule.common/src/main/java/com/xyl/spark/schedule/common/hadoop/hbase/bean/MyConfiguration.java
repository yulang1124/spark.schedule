package com.xyl.spark.schedule.common.hadoop.hbase.bean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by XXX on 2017/8/29.
 */

@Component
public class MyConfiguration {
    private Configuration configuration;

    @Value("${zookeeper.ip}")
    private String ip;
    @Value("${zookeeper.port}")
    private String port;

    @PostConstruct
    public void init(){
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", ip);
        configuration.set("hbase.zookeeper.property.clientPort", port);
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
