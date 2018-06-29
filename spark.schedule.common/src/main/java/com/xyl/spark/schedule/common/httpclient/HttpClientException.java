package com.xyl.spark.schedule.common.httpclient;

/**
 * Created by XXX on 2017/9/26.
 */
public class HttpClientException extends Exception {

    private static final long serialVersionUID = 1L;

    public HttpClientException(String msg, Throwable e) {
        super(msg, e);
    }
}
