package com.sky.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP 客户端工具类
 * 提供 GET、POST 等常用请求方法
 */
public class HttpClientUtil {

    // 单例 HttpClient 实例（线程安全，全局共享）
    private static volatile CloseableHttpClient httpClient;

    // 默认字符集
    private static final String DEFAULT_CHARSET = "UTF-8";

    // 默认连接超时时间（毫秒）
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;

    // 默认读取超时时间（毫秒）
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;

    // 默认从连接池获取连接的超时时间（毫秒）
    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 5000;

    // 私有构造函数，防止外部实例化
    private HttpClientUtil() {}

    /**
     * 获取 HttpClient 实例（双重检查锁单例模式）
     *
     * @return CloseableHttpClient 实例
     */
    private static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (HttpClientUtil.class) {
                if (httpClient == null) {
                    httpClient = createHttpClient();
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建配置好的 HttpClient 实例
     *
     * @return CloseableHttpClient
     */
    private static CloseableHttpClient createHttpClient() {
        // 连接池配置
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);  // 最大连接数
        connectionManager.setDefaultMaxPerRoute(50);  // 每个路由的最大连接数

        // 请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)  // 连接超时
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)    // 读取超时
                .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)  // 获取连接超时
                .build();

        // 创建 HttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求地址
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doGet(String url) throws IOException {
        return doGet(url, null);
    }

    /**
     * 发送 GET 请求（带请求头）
     *
     * @param url 请求地址
     * @param headers 请求头（可为null）
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doGet(String url, Map<String, String> headers) throws IOException {
        HttpGet httpGet = new HttpGet(url);

        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置 User-Agent（防止某些服务器拒绝访问）
        if (headers == null || !headers.containsKey("User-Agent")) {
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        }

        try (CloseableHttpResponse response = getHttpClient().execute(httpGet)) {
            return handleResponse(response);
        }
    }

    /**
     * 发送 POST 请求（表单提交，application/x-www-form-urlencoded）
     *
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostForm(String url, Map<String, String> params) throws IOException {
        return doPostForm(url, params, null);
    }

    /**
     * 发送 POST 请求（表单提交，带请求头）
     *
     * @param url 请求地址
     * @param params 请求参数
     * @param headers 请求头（可为null）
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置 Content-Type
        if (headers == null || !headers.containsKey("Content-Type")) {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET);
        }

        // 构建表单参数
        if (params != null && !params.isEmpty()) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
        }

        try (CloseableHttpResponse response = getHttpClient().execute(httpPost)) {
            return handleResponse(response);
        }
    }

    /**
     * 发送 POST 请求（JSON 格式）
     *
     * @param url 请求地址
     * @param jsonBody JSON 字符串
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostJson(String url, String jsonBody) throws IOException {
        return doPostJson(url, jsonBody, null);
    }

    /**
     * 发送 POST 请求（JSON 格式，带请求头）
     *
     * @param url 请求地址
     * @param jsonBody JSON 字符串
     * @param headers 请求头（可为null）
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostJson(String url, String jsonBody, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置 Content-Type
        if (headers == null || !headers.containsKey("Content-Type")) {
            httpPost.setHeader("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        }

        // 设置 JSON 请求体
        if (jsonBody != null && !jsonBody.isEmpty()) {
            httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        }

        try (CloseableHttpResponse response = getHttpClient().execute(httpPost)) {
            return handleResponse(response);
        }
    }

    /**
     * 发送 POST 请求（XML 格式）
     *
     * @param url 请求地址
     * @param xmlBody XML 字符串
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostXml(String url, String xmlBody) throws IOException {
        return doPostXml(url, xmlBody, null);
    }

    /**
     * 发送 POST 请求（XML 格式，带请求头）
     *
     * @param url 请求地址
     * @param xmlBody XML 字符串
     * @param headers 请求头（可为null）
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostXml(String url, String xmlBody, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置 Content-Type
        if (headers == null || !headers.containsKey("Content-Type")) {
            httpPost.setHeader("Content-Type", "application/xml; charset=" + DEFAULT_CHARSET);
        }

        // 设置 XML 请求体
        if (xmlBody != null && !xmlBody.isEmpty()) {
            httpPost.setEntity(new StringEntity(xmlBody, StandardCharsets.UTF_8));
        }

        try (CloseableHttpResponse response = getHttpClient().execute(httpPost)) {
            return handleResponse(response);
        }
    }

    /**
     * 发送 POST 请求（原始字符串格式）
     *
     * @param url 请求地址
     * @param body 请求体字符串
     * @param contentType Content-Type 类型
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostRaw(String url, String body, String contentType) throws IOException {
        return doPostRaw(url, body, contentType, null);
    }

    /**
     * 发送 POST 请求（原始字符串格式，带请求头）
     *
     * @param url 请求地址
     * @param body 请求体字符串
     * @param contentType Content-Type 类型
     * @param headers 额外请求头（可为null）
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    public static String doPostRaw(String url, String body, String contentType, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 设置 Content-Type
        httpPost.setHeader("Content-Type", contentType + "; charset=" + DEFAULT_CHARSET);

        // 设置请求体
        if (body != null && !body.isEmpty()) {
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }

        try (CloseableHttpResponse response = getHttpClient().execute(httpPost)) {
            return handleResponse(response);
        }
    }

    /**
     * 处理 HTTP 响应
     *
     * @param response HTTP 响应对象
     * @return 响应内容字符串
     * @throws IOException IO异常
     */
    private static String handleResponse(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            // 检查响应状态码
            if (statusCode >= 200 && statusCode < 300) {
                return result;
            } else {
                throw new IOException("HTTP 请求失败，状态码: " + statusCode + ", 响应内容: " + result);
            }
        } else {
            if (statusCode >= 200 && statusCode < 300) {
                return "";
            } else {
                throw new IOException("HTTP 请求失败，状态码: " + statusCode);
            }
        }
    }

    /**
     * 关闭 HttpClient（应用关闭时调用）
     */
    public static void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}