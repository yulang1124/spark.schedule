package com.xyl.spark.schedule.common.hadoop.hbase.service;


import com.xyl.spark.schedule.common.hadoop.hbase.Identification.Mark;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.MyConfiguration;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;
import com.xyl.spark.schedule.common.hadoop.hbase.util.ParseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.mapred.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by XXX on 2017/8/26.
 */
@Service
@Scope("prototype")
public class InsertImpl implements InsertInterface {

    @Resource
    private MyConfiguration myConfiguration;


    public void putRow(String tableName,  Row row){
        Connection connection = null;
        try {
            Configuration conf = myConfiguration.getConfiguration();
            conf.set(TableOutputFormat.OUTPUT_TABLE, tableName);
            connection = ConnectionFactory.createConnection(conf);
            Table table = connection.getTable(TableName.valueOf(tableName));
            String rowkey = row.getRowkey();
            Put put = new Put(Bytes.toBytes(rowkey));
            Set<Map.Entry<String, String>> es = row.getColumn_values().entrySet();
            for(Map.Entry<String, String> e:es){
                put.addColumn(Bytes.toBytes(Mark.HBase.FANILIE), Bytes.toBytes(e.getKey()), System.currentTimeMillis(), Bytes.toBytes(e.getValue()));
            }
            table.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putRows(String tableName, List<Row> rows){

        List<Put> putList = ParseUtil.parsePuts(rows);
        try {
            Configuration conf = myConfiguration.getConfiguration();
            conf.set(TableOutputFormat.OUTPUT_TABLE, tableName);
            Connection connection = ConnectionFactory.createConnection(conf);
            Table table = connection.getTable(TableName.valueOf(tableName));
            table.put(putList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
