package com.xyl.spark.schedule.common.hadoop.hbase.exception;

/**
 * Created by XXX on 2017/8/25.
 */
public class TableExistsException  extends  Exception{
    public TableExistsException() {
        super();
    }

    public TableExistsException(String message) {
        super(message);
    }
}
