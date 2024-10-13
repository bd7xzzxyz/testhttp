package com.example.controller;

import com.example.DispatcherHandler;
import com.example.service.UserService;

import java.util.Map;

public class RegisterController implements DispatcherHandler {


    @Override
    public String handlePost(Map<String, String> params) {
        UserService.getInstance().register(params.get("username"), params.get("password"));
        return "success";
    }

    @Override
    public String handleGet(Map<String, String> params) {
        return "";
    }
}
