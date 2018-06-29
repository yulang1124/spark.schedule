package com.xyl.spark.schedule.common.hadoop.hbase.service;



import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;

import java.util.List;

/**
 * Created by XXX on 2017/8/27.
 */
public interface DeleteInterface {
    void deleteRow(String tableName, String rowkey);

    void deleteRow(String tableName, Row row);

    void deleteRows(String tableName, List<String> rowkeys);
}
