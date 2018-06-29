package com.xyl.spark.schedule.common.hadoop.hbase.service;

import com.xyl.spark.schedule.common.hadoop.hbase.Identification.Mark;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.MyConfiguration;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;
import com.xyl.spark.schedule.common.hadoop.hbase.util.ParseUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
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
public class DeleteImpl implements DeleteInterface {

    @Resource
    private MyConfiguration myConfiguration;



    public void deleteRow(String tableName, String rowkey){
        try {
            Connection connection = ConnectionFactory.createConnection(myConfiguration.getConfiguration());
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(rowkey));
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteRow(String tableName, Row row){
        try {
            Connection connection = ConnectionFactory.createConnection(myConfiguration.getConfiguration());
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(row.getRowkey()));
            Set<Map.Entry<String, String>> es = row.getColumn_values().entrySet();
            for(Map.Entry<String, String> e:es){
                delete.addColumn(Bytes.toBytes(Mark.HBase.FANILIE), Bytes.toBytes(e.getKey()));
            }
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteRows(String tableName, List<String> rowkeys){
        List<Delete> deletes = ParseUtil.stringParseDeletes(rowkeys);
        try {
            Connection connection = ConnectionFactory.createConnection(myConfiguration.getConfiguration());
            Table table = connection.getTable(TableName.valueOf(tableName));
            table.delete(deletes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
