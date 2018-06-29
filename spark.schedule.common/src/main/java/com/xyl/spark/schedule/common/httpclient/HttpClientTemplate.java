package com.xyl.spark.schedule.common.httpclient;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.springframework.stereotype.Service;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by XXX on 2017/9/26.
 */

@Service
public class HttpClientTemplate {
    Log log = LogFactory.getLog(getClass());


    public HttpClientSession createHttps(String resourcePath, String password){
        InputStream in = null;
        try {
            if(StringUtils.startsWith(resourcePath, "classpath:")){
                // 类路径
                String classPath = StringUtils.substringAfter(resourcePath, "classpath:");
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPath);
            }else {
                //本地文件
                in = new FileInputStream(new File(resourcePath));
            }
            return this.createHttps(in, password);
        }catch (Exception e){
            log.error("Resource not found! ResourcePath: " + resourcePath, e);
            return null;
        }finally {
            IOUtils.closeQuietly(in);
        }
    }


    /**
     * 创建Https客户端会话
     *
     * @param charset
     * @return
     */
    public HttpClientSession createHttps(String charset){
        return new HttpClientSession(HttpClients.createDefault(), charset);
    }

    /**
     * 创建Https客户端会话
     *
     * @return
     */
    public HttpClientSession createHttps(){
        return new HttpClientSession(HttpClients.createDefault());
    }

    /**
     * 创建Https客户端会话
     *
     * @param in
     * @param password
     * @return
     */
    public HttpClientSession createHttps(InputStream in, String password){
        SSLContext sslContext = custom(in, password);
        Registry<ConnectionSocketFactory> socketFactoryRegistry =  RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                })).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return  new HttpClientSession(HttpClients.custom().setConnectionManager(connectionManager).build(), "UTF-8");
    }


    /**
     * 设置信任自签名证书
     *
     * @param in 密钥库二进制流
     * @param password 密钥库密码
     * @return
     */
    private SSLContext custom(InputStream in, String password){
        SSLContext sslContext = null;
        KeyStore trustStore = null;
        try{
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(in, password.toCharArray());
            //相信自己的CA和所有自签名的证书
            sslContext = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException | KeyManagementException e) {
            log.error("SSLContext create failed", e);
        }
        return sslContext;
    }

}
