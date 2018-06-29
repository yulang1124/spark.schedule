package com.xyl.spark.schedule.common.hadoop.hbase.api;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by XXX on 2017/8/27.
 */
public class HBaseContext {


    //qiaofangApplicationContext
    private static final String FACTORY_KEY = "./beanRefContext.xml";

    public static HBaseClient getClient(){
        ApplicationContext context = new ClassPathXmlApplicationContext(FACTORY_KEY);
        return context.getBean(HBaseClient.class);
    }
}
