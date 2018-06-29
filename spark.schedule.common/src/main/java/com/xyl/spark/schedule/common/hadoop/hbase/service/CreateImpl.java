package com.xyl.spark.schedule.common.hadoop.hbase.service;

import com.xyl.spark.schedule.common.hadoop.hbase.Identification.Mark;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.MyConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created by XXX on 2017/8/26.
 */

@Service
public class CreateImpl implements CreateInterface {

    @Resource
    private MyConfiguration configuration;

    public void createTable(String tableName){

        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(configuration.getConfiguration());
            Admin admin = connection.getAdmin();
            if(admin.tableExists(TableName.valueOf(tableName))){
                throw new TableExistsException(tableName +" is exists");
            }else{
                HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(tableName.getBytes()));
                htd.addFamily(new HColumnDescriptor(Bytes.toBytes(Mark.HBase.FANILIE)));
                admin.createTable(htd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
