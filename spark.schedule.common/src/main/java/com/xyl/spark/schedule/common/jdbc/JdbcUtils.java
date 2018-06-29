package com.xyl.spark.schedule.common.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xyl.spark.schedule.common.utils.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XXX on 2017/9/20.
 */
public final class JdbcUtils {

    private static Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String, JdbcTemplate>();

    private static final String JDBC_DRIVER = ".jdbc.driver";
    private static final String JDBC_URL = ".jdbc.url";
//    private static final String JDBC_USERNAME = ".jdbc.username";
//    private static final String JDBC_PASSWORD = ".jdbc.password";

    static Log log = LogFactory.getLog(JdbcUtils.class);
    static boolean[] syncObject = new boolean[0];

    public  static JdbcTemplate getJdbcTemplate(String instanceName){
        if(jdbcTemplateMap.containsKey(instanceName)){
            return jdbcTemplateMap.get(instanceName);
        }

        synchronized (syncObject){
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            try {
                dataSource.setDriverClass(ConfigUtils.getProperty(instanceName + JDBC_DRIVER));
            } catch (PropertyVetoException e) {
                log.error("set driver failed!", e);
            }
            dataSource.setJdbcUrl(ConfigUtils.getProperty(instanceName + JDBC_URL));
//            dataSource.setUser(ConfigUtils.getProperty(instanceName + JDBC_USERNAME));
//            dataSource.setPassword(ConfigUtils.getProperty(instanceName + JDBC_PASSWORD));
            dataSource.setTestConnectionOnCheckin(false);
            dataSource.setTestConnectionOnCheckout(true);
            dataSource.setMaxPoolSize(18);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplateMap.put(instanceName, jdbcTemplate);
            return jdbcTemplate;
        }
    }


    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate("impala");
        List<String> list = jdbcTemplate.queryForList("select * from dc_road limit 10", null, new RowMapper<String>() {
            @Override
            public String parseResult(ResultSet result, int rowNum) throws SQLException {
                return result.getString("city");
            }
        });
        for(String s:list){
            System.out.println(s);
        }
    }

}
