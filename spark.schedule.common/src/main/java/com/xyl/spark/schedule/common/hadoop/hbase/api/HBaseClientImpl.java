package com.xyl.spark.schedule.common.hadoop.hbase.api;


import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;
import com.xyl.spark.schedule.common.hadoop.hbase.service.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xia on 16/4/19.
 */

@Service
@Scope("prototype")
public class HBaseClientImpl implements HBaseClient {


    @Resource
    private CreateInterface create;
    @Resource
    private DropInterface drop;
    @Resource
    private InsertInterface insert;
    @Resource
    private DeleteInterface delete;
    @Resource
    private SelectInterface select;


    public HBaseClientImpl() {
    }

    public void createTable(String tableName) {
        create.createTable(tableName);
    }

    public void dropTable(String tableName) {
        drop.dropTable(tableName);
    }

    public void putRow(String tableName, Row row) {
        insert.putRow(tableName, row);
    }

    public void putRows(String tableName, List<Row> rows) {
        insert.putRows(tableName, rows);
    }

    public void delete(String tableName, String rowKey) {
        delete.deleteRow(tableName, rowKey);
    }

    public void delete(String tableName, Row row) {
        delete.deleteRow(tableName, row);
    }

    public void delete(String tableName, List<String> rowkeys) {
        delete.deleteRows(tableName, rowkeys);
    }

    public Row getRow(String tableName, String rowkey) {
        return select.getRow(tableName, rowkey);
    }

    /**
     * 保留, 因为从HBase读取太多数据时 解释很耗时 而且可能会oom
     *
     * @param tableName
     * @param startKey
     * @param endKey
     * @return
     */
    public List<Row> getRows(String tableName, String startKey, String endKey) {
        return select.getRows(tableName, startKey, endKey);
    }


    public void setCreate(CreateInterface create) {
        this.create = create;
    }

    public void setDrop(DropInterface drop) {
        this.drop = drop;
    }

    public void setInsert(InsertInterface insert) {
        this.insert = insert;
    }

    public void setDelete(DeleteInterface delete) {
        this.delete = delete;
    }

    public void setSelect(SelectInterface select) {
        this.select = select;
    }

}
