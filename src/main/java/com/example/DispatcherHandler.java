package com.example;

import java.util.Map;

/**
 * 请求分发处理器
 */
public interface DispatcherHandler{


    /**
     * 处理POST请求
     * @param params 请求参数
     * @return http response body
     */
    String handlePost(Map<String, String> params);

    /**
     * 处理GET请求
     * @param params 请求参数
     * @return http response body
     */
    String handleGet(Map<String, String> params);

}
