package com.xyl.spark.schedule.common.hadoop.hbase.bean;

import java.util.Map;

/**
 * Created by XXX on 2017/8/25.
 */
public class Row {
    public String rowkey;
    public Map<String, String> column_values;
    public Row(){

    }
    public Row(String rowkey, Map<String, String> column_values){
        this.rowkey = rowkey;
        this.column_values = column_values;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public Map<String, String> getColumn_values() {
        return column_values;
    }

    public void setColumn_values(Map<String, String> column_values) {
        this.column_values = column_values;
    }
}
