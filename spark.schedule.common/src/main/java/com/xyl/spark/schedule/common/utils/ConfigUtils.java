package com.xyl.spark.schedule.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by XXX on 2017/9/20.
 */
public final class ConfigUtils {
    private static final Properties prop = new Properties();
    static Log log = LogFactory.getLog(ConfigUtils.class);

    static {

        try {
//            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));

            prop.load(new FileInputStream("conf/db.properties"));
        } catch (IOException e) {
            log.error("jdbc.properties load failed!", e);
        }
    }

    public static String getProperty(String key){
        return prop.getProperty(key);
    }

    private ConfigUtils()
    {
    }
}
