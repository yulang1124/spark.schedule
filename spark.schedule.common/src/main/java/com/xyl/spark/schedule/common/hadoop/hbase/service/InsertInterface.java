package com.xyl.spark.schedule.common.hadoop.hbase.service;



import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;

import java.util.List;

/**
 * Created by XXX on 2017/8/27.
 */
public interface InsertInterface {
    void putRow(String tableName, Row row);
    void putRows(String tableName, List<Row> rows);
}
