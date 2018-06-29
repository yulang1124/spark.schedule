package com.xyl.spark.schedule.common.hadoop.hbase.service;

import com.xyl.spark.schedule.common.hadoop.hbase.bean.MyConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by XXX on 2017/8/26.
 */
@Service
public class DropImpl implements DropInterface {


    @Resource
    private MyConfiguration myConfiguration;

    public void dropTable(String tableName) {
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(myConfiguration.getConfiguration());
            Admin admin = connection.getAdmin();
            if (admin.tableExists(TableName.valueOf(tableName))) {
                admin.disableTable(TableName.valueOf(tableName));
                admin.deleteTable(TableName.valueOf(tableName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
