package com.xyl.spark.schedule.common.jdbc;

import java.sql.PreparedStatement;

/**
 * Created by XXX on 2017/9/20.
 */
public interface PreperstatementSetter<T> {
    void setParams(PreparedStatement preparedStatement, T param);
}
