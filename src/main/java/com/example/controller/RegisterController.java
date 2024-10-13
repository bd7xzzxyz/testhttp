package com.example.controller;

import com.example.DispatcherHandler;
import com.example.exception.BizError;
import com.example.exception.BizException;
import com.example.service.UserService;

import java.util.Map;

/**
 * 注册controller
 */
public class RegisterController implements DispatcherHandler {

    @Override
    public String handlePost(Map<String, String> params) {
        UserService.getInstance().register(params.get("username"), params.get("password")); //执行注册逻辑
        return "success"; //注册成功返回success
    }

    @Override
    public String handleGet(Map<String, String> params) {
        throw new BizException(BizError.NOT_SUPPORT); //不支持GET请求
    }
}
