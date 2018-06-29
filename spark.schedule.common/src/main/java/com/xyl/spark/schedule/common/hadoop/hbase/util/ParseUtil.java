package com.xyl.spark.schedule.common.hadoop.hbase.util;

import com.xyl.spark.schedule.common.hadoop.hbase.Identification.Mark;
import com.xyl.spark.schedule.common.hadoop.hbase.bean.Row;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;

/**
 * 数据转换成 Put Delete
 * Created by XXX on 2017/8/25.
 */
public class ParseUtil {


    public static List<Put> parsePuts(List<Row> rows) {
        List<Put> list = new ArrayList<Put>();
        for (Row row : rows) {
            String rowkey = row.getRowkey();
            Put put = new Put(Bytes.toBytes(rowkey));
            if (!row.getColumn_values().isEmpty() || row.getColumn_values() != null) {
                Set<Map.Entry<String, String>> es = row.getColumn_values().entrySet();
                for (Map.Entry<String, String> e : es) {
                    put.addColumn(Bytes.toBytes(Mark.HBase.FANILIE), Bytes.toBytes(e.getKey()), System.currentTimeMillis(), Bytes.toBytes(e.getValue()));
                }
            }
            list.add(put);
        }
        return list;
    }

//    public static List<Delete> cellParseDeletes(List<Row> rows) {
//        List<Delete> list = new ArrayList<Delete>();
//        for (Row row : rows) {
//            String rowkey = row.getRowkey();
//            Delete delete = new Delete(Bytes.toBytes(rowkey));
//            if (!row.getColumn_values().isEmpty() || row.getColumn_values() != null) {
//                Set<Map.Entry<String, String>> es = row.getColumn_values().entrySet();
//                for (Map.Entry<String, String> e : es) {
//                    delete.addColumn(Bytes.toBytes(Mark.HBase.FANILIE), Bytes.toBytes(e.getKey()));
//                }
//                list.add(delete);
//            }
//        }
//        return list;
//    }
//
//
//    public static List<Delete> stringParseDeletes(List<String> rowkeys) {
//        List<Delete> list = new ArrayList<Delete>();
//        for (String rowkey : rowkeys) {
//            String tempKey = rowkey;
//            Delete delete = new Delete(Bytes.toBytes(tempKey));
//            list.add(delete);
//        }
//        return list;
//    }



    public static List<Delete> cellParseDeletes(List<Row> rows) {
        List<Delete> deleteList = new ArrayList<Delete>();
        for(Row row:rows){
            deleteList.add(parseDelete(row.getRowkey()));
        }
        return deleteList;
    }

    public static List<Delete> stringParseDeletes(List<String> rows) {
        List<Delete> deleteList = new ArrayList<Delete>();
        for(String row:rows){
            deleteList.add(parseDelete(row));
        }
        return deleteList;
    }

    public static Delete parseDelete(String rowkey){
        return new Delete(Bytes.toBytes(rowkey));
    }

    public static Row resultParseRow(Result result) {
        NavigableMap map = result.getFamilyMap(Bytes.toBytes(Mark.HBase.FANILIE));
        Map<String, String> column_value = new HashMap<String, String>();
        if (map == null) {
            return null;
        }
        Set<Map.Entry<byte[], byte[]>> entrys = map.entrySet();
        for (Map.Entry<byte[], byte[]> entry : entrys) {
            column_value.put(Bytes.toString(entry.getKey()), Bytes.toString(entry.getValue()));
        }
        String rowkey = Bytes.toString(result.getRow());
        return new Row(rowkey, column_value);
    }

}
