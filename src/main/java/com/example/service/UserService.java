package com.example.service;

import com.example.dao.UserDao;

public class UserService {

    private static final UserDao userDao = new UserDao();

    public void register(String userName, String password) {
        checkParam(userName, password);
        userDao.addUser(userName, password);
    }

    public boolean login(String userName, String password) {
        checkParam(userName, password);
        return userDao.login(userName, password);
    }

    private void checkParam(String userName, String password) {
        if (null == userName || userName.trim().isEmpty()) {
            throw new RuntimeException("invalid user name");
        }
        if (null == password || password.trim().isEmpty()) {
            throw new RuntimeException("invalid password");
        }
    }
}
