package com.xyl.spark.schedule.common.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by XXX on 2017/9/20.
 */
public interface RowMapper<T> {

    T parseResult(ResultSet result, int rowNum) throws SQLException;
}
