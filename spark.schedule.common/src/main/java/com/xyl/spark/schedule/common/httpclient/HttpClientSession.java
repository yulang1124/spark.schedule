package com.xyl.spark.schedule.common.httpclient;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * Created by XXX on 2017/9/26.
 */
public class HttpClientSession implements Closeable {

    public static final int SOCKET_TIMEOUT = 20000;
    public static final int CONNECT_TIMEOUT = 20000;

    Log log = LogFactory.getLog(HttpClientSession.class);

    private CloseableHttpClient httpClient;

    private String charset = "UTF-8";

    private final Map<String, String> headerMap = Collections.synchronizedMap(new HashMap<String, String>());



    protected HttpClientSession(CloseableHttpClient httpClient){
        if(httpClient == null){
            throw new IllegalArgumentException("httpClient cannot null!");
        }
        this.httpClient = httpClient;
    }

    protected HttpClientSession(CloseableHttpClient httpClient, String charset){
        if(httpClient == null){
            throw new IllegalArgumentException("httpClient cannot null!");
        }
        if(!StringUtils.isBlank(charset)){
            this.charset = charset;
        }
        this.httpClient = httpClient;
    }



    /**
     * 获取头参数
     *
     */
    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    /**
     * 向指定URL发送同步GET请求
     *
     * @param url
     * @return
     */
    public String get(String url) throws HttpClientException{
        return get(url, null);
    }

    /**
     * 向指定URL发送同步GET请求
     *
     * @param url
     * @param proxy
     * @return
     * @throws HttpClientException
     */

    public String get(String url, Proxy proxy) throws HttpClientException{
        CloseableHttpResponse response = null;
        try{
            HttpGet httpGet = new HttpGet(url);
            Builder builder = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT);
            if(proxy != null){
                // 设置http代理
                HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort());
                builder.setProxy(httpHost);
            }
            httpGet.setConfig(builder.build());
            for(Map.Entry<String, String> entry:headerMap.entrySet()){
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }

            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, charset);

        }catch (Exception e){
            throw new HttpClientException("asyncGet failed", e);
        }finally {
            IOUtils.closeQuietly(response);
        }
    }

    /**
     * 向指定URL发送同步POST请求
     *
     * @param url
     * @param content
     * @return
     * @throws HttpClientException
     */
    public String post(String url, Object content) throws HttpClientException{
        if(content == null){
            throw  new IllegalArgumentException("content cannot null");
        }
        String json = null;
        if(content instanceof String){
            json = (String) content;
        }else {
            json = JSON.toJSONString(content);
        }
        return post(url, null, json);
    }

    /**
     * 通过POST提交一个表单
     *
     * @param url
     * @param form
     * @return
     */
    public String post(String url, Map<String, String> form) throws HttpClientException{
        CloseableHttpResponse response = null;
        try {

            HttpPost post = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
            post.setConfig(requestConfig);
            for(Map.Entry<String, String> entry:headerMap.entrySet()){
                post.addHeader(entry.getKey(), entry.getValue());
            }
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            if(form != null && form.size() >0){
                for(Map.Entry<String, String> entry:form.entrySet()){
                    nameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList, charset);
            post.setEntity(urlEncodedFormEntity);
            response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity());

        }catch (Exception e){
            throw new IllegalArgumentException("post failed!", e);
        }finally {
            IOUtils.closeQuietly(response);
        }
    }

    /**
     * 向指定URL发送同步POST请求
     *
     * @param url
     * @param proxy
     * @param content
     * @return
     * @throws HttpClientException
     */
    public String post(String url, Proxy proxy, String content) throws HttpClientException{
        CloseableHttpResponse response = null;
        try {

            HttpPost post = new HttpPost(url);
            Builder builder = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT);
            if(proxy != null){
                //设置http代理
                HttpHost httpHost = new HttpHost(proxy.getIp(), proxy.getPort());
                builder.setProxy(httpHost);
            }
            post.setConfig(builder.build());
            for(Map.Entry<String, String> entry:headerMap.entrySet()){
                post.addHeader(entry.getKey(), entry.getValue());
            }
            StringEntity entity = new StringEntity(content, charset);
            entity.setContentType("application/json");
            response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity(), charset);

        }catch (Exception e){
            throw new HttpClientException("asyncPost failed!", e);
        }finally {
            IOUtils.closeQuietly(response);
        }
    }



    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(httpClient);
    }



}
