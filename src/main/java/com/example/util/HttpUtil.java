package com.example.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析Http协议相关的工具类
 */
public class HttpUtil {

    private HttpUtil() {
    }

    /**
     * 从客户端请求流中读取http协议
     * @param in 客户端请求流对象
     * @return http请求字符串
     */
    public static String parseRequest(BufferedReader in) {
        try {
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            while (!(line = in.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n"); //http协议使用\r\n作为分隔符
            }
            return requestBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace(); //打印异常栈
            return ""; //返回空，由上层决策如何处理异常
        }
    }

    /**
     * 读取请求体
     * @param in
     * @return
     */
    public static String readBody(BufferedReader in) {
        try {
            StringBuilder bodyBuilder = new StringBuilder();
            while (in.ready()) {
                bodyBuilder.append((char) in.read());
            }
            return bodyBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace(); //打印异常栈
            return ""; //返回空，由上层决策如何处理异常
        }
    }

    /**
     * 解析请求参数
     * @param body 请求体
     * @return 请求参数kv
     */
    public static Map<String, String> parseParams(String body) {
        if (body.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> params = new HashMap<>();
        String[] pairs = body.split("&"); //处理多个参数，http多参数使用&拼接
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");//提取key value
            if (keyValue.length == 2) {
                try {
                    params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name()), //已utf-8字符集解析请求参数
                            URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    //ignored
                }
            }
        }
        return params;
    }

    /**
     * 发送响应报文
     * @param out 输出流对象
     * @param statusCode http response 状态码
     * @param statusText http response 状态码简述
     * @param body  http response body
     */
    public static void sendResponse(PrintWriter out, int statusCode, String statusText, String body) {
        out.printf("HTTP/1.1 %d %s\r\n", statusCode, statusText); //拼接response header
        out.println("Content-Type: text/plain; charset=UTF-8"); //设置字符集
        if (null == body || body.isEmpty()) { //如果没有body设置header的Content-length为0
            out.println("Content-Length: 0");
            out.println();
        } else { //根据body长度设置Content-length
            out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
            out.println();
            out.println(body); //输出body
        }
    }
}
