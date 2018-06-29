package com.xyl.spark.schedule.common.hadoop.hbase.api;


import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;

import java.util.List;

/**
 * Created by XXX on 2017/8/27.
 */

public interface HBaseClient {
    void createTable(String tableName);

    void dropTable(String tableName);

    void putRow(String tableName, Row row);

    void putRows(String tableName, List<Row> rows);

    void delete(String tableName, String rowKey);

    void delete(String tableName, Row row);

    void delete(String tableName, List<String> rowkeys);

    Row getRow(String tableName, String rowkey);

    /**
     * 保留, 因为从HBase读取太多数据时 解释很耗时 而且可能会oom
     * @param tableName
     * @param startKey
     * @param endKey
     * @return
     */
    List<Row> getRows(String tableName, String startKey, String endKey);

}
