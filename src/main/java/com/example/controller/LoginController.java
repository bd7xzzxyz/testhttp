package com.example.controller;

import com.example.DispatcherHandler;
import com.example.exception.BizError;
import com.example.exception.BizException;
import com.example.service.UserService;

import java.util.Map;

/**
 * 登录controller
 */
public class LoginController implements DispatcherHandler {

    @Override
    public String handlePost(Map<String, String> params) {
        String username = params.get("username"); //获取用户名
        boolean success = UserService.getInstance().login(username, params.get("password")); //处理登录业务逻辑
        return success ? "hello " + username : "username or password error!"; //返回hello 用户名或用户名密码错误
    }

    @Override
    public String handleGet(Map<String, String> params) {
        throw new BizException(BizError.NOT_SUPPORT); //不支持GET请求
    }
}
