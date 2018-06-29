package com.xyl.spark.schedule.common.hadoop.hbase.bean;

import org.apache.hadoop.hbase.client.Result;

import java.util.Iterator;

/**
 * 保留
 * Created by XXX on 2017/8/25.
 */
public class Results {

//    private ResultScanner resultScanner;
    private Iterator<Result> iters;

    public Results(){

    }
//    public Results(ResultScanner resultScanner){
//        this.resultScanner = resultScanner;
//    }
     public Results(Iterator<Result> iters){
        this.iters = iters;
    }

    public Row next(){

        Row row = null;
//        try {
//            resultScanner.iterator();
//            row = ParseUtil.resultParseRow(resultScanner.next());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if(iters.hasNext()){
            iters.next();
        }
        return row;

    }

}
