package com.xyl.spark.schedule.common.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XXX on 2017/9/20.
 */

public final class JdbcTemplate {

    private DataSource dataSource;

    Log log = LogFactory.getLog(getClass());

    public JdbcTemplate(DataSource dataSource){
        if(dataSource == null){
            throw  new IllegalArgumentException("dataSource can not null!");
        }
        this.dataSource = dataSource;
    }

    /**
     * 执行一条更新语句
     *
     * @param sql
     * @param params
     */
    public void executeUpdate(String sql, Object[] params){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if(params != null && params.length > 0){
                for(int i = 0; i < params.length; i++){
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            log.error("executeUpdate failed! sql: " + sql, e);

        }finally {
            close(preparedStatement);
            close(connection);
        }
    }

    /**
     * 执行批量更新
     * @param sql
     * @param params
     * @param preperstatementSetter
     * @param <T>
     */
    public <T> void batchUpdate(String sql, List<T> params, PreperstatementSetter<T> preperstatementSetter){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            //返回可滚动数据集，当数据库变化时，当前结果集同步改变。不能用结果集更新数据库中的表。
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            for(T param:params){
                preperstatementSetter.setParams(preparedStatement, param);
            }
            preparedStatement.executeBatch();
            connection.commit();
        } catch (Exception e) {
            log.error("batchUpdate failed! sql: " + sql, e);
        }finally {
            close(preparedStatement);
            close(connection);
        }
    }

    /**
     * 查询列表
     *
     * @param sql
     * @param params
     * @param rowMapper
     * @param <T>
     * @return
     */
    public <T> List<T> queryForList(String sql, Object[] params, RowMapper<T> rowMapper){

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {

            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            if(params != null && params.length >0){
                for(int i = 0; i < params.length; i++){
                    preparedStatement.setObject(i + 1, params[i]);
                }
            }
            rs = preparedStatement.executeQuery();
            int rowNum = 0;
            List<T> list = new ArrayList<T>();
            while (rs.next()) {
                T row = rowMapper.parseResult(rs, rowNum);
                rowNum++;
                if(row != null){
                    list.add(row);
                }
            }
            return list;
        } catch (Exception e) {
            log.error("queryForList failed! sql: " + sql, e);
            return null;
        }finally {
            close(rs);
            close(preparedStatement);
            close(connection);
        }
    }




    private void close(AutoCloseable autoCloseable){
        if(autoCloseable != null){
            try {
                autoCloseable.close();
            } catch (Exception e) {
                log.warn("close faield!", e);
            }
        }
    }

}
