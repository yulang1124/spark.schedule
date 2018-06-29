package com.xyl.spark.schedule.common.hadoop.hbase.service;



import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;

import java.util.List;

/**
 * Created by XXX on 2017/8/27.
 */
public interface SelectInterface {
    Row getRow(String tableName, String rowkey);
    List<Row> getRows(String tableName, String startRow, String endRow);

}
