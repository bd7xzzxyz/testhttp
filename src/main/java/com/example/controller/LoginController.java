package com.example.controller;

import com.example.DispatcherHandler;
import com.example.service.UserService;

import java.util.Map;

public class LoginController implements DispatcherHandler {

    @Override
    public String handlePost(Map<String, String> params) {
        String username = params.get("username");
        boolean success = UserService.getInstance().login(username, params.get("password"));
        return success ? "hello " + username : "username or password error!";
    }

    @Override
    public String handleGet(Map<String, String> params) {
        return "";
    }
}
