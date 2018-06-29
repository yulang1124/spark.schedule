package com.xyl.spark.schedule.common.hadoop.hbase.service;

import com.xyl.spark.schedule.common.hadoop.hbase.Identification.Mark;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.MyConfiguration;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;
import com.xyl.spark.schedule.common.hadoop.hbase.util.ParseUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XXX on 2017/8/26.
 */
@Service
public class SelectImpl implements SelectInterface {
    @Resource
    private MyConfiguration myConfiguration;

    public Row getRow(String tableName, String rowkey){
        Row row = null;
        try {
            Connection connection = ConnectionFactory.createConnection(myConfiguration.getConfiguration());
            Table table = connection.getTable(TableName.valueOf(tableName));
            String tempKey = rowkey;
            Get get = new Get(Bytes.toBytes(tempKey));
            Result rs = table.get(get);
            if(rs != null){
                row = ParseUtil.resultParseRow(rs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return row;
    }


    /**
     * 保留, 问题：startkey 和 end前面的分区策略会导致查询出来的数据不全
     * @param tableName
     * @return
     */
    public List<Row> getRows(String tableName, String startRow, String endRow){
        ResultScanner rss = null;
        List<Row> result = new ArrayList<Row>();
        try {
            Connection connection = ConnectionFactory.createConnection(myConfiguration.getConfiguration());
            Table table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            String tempStartKey = startRow;
            String tempEndKey = endRow;
            scan.setStartRow(Bytes.toBytes(tempStartKey));
            scan.setStopRow(Bytes.toBytes(tempEndKey));
            //考虑是result否暴露出来 涉及道查询优化
            scan.setCaching(1000);
//            scan.setBatch(5);
            scan.setRaw(false);
            //----------------------------
            scan.addFamily(Bytes.toBytes(Mark.HBase.FANILIE));
            rss = table.getScanner(scan);
            Result rs = rss.next();
            while (rs != null){
                result.add(ParseUtil.resultParseRow(rs));
                rs = rss.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result.size() == 0){
            return null;
        }
        return result;
    }
}
