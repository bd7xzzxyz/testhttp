package com.example.service;

import com.example.dao.UserDao;
import com.example.entity.UserEntity;
import com.example.exception.BizError;
import com.example.exception.BizException;

/**
 * 用户服务
 */
public class UserService {

    private static final UserService INSTANCE = new UserService(); //单例对象
    private static final UserDao userDao = UserDao.getInstance(); //user dao对象

    private UserService() {
    }

    /**
     * 获取UserService对象
     * @return UserService对象
     */
    public static UserService getInstance() {
        return INSTANCE;
    }

    /**
     * 注册
     * @param userName 用户名
     * @param password 密码
     */
    public void register(String userName, String password) {
        checkParam(userName, password); //校验参数
        try {
            userDao.addUser(userName, password); //调用dao写入数据库
        } catch (Exception e) {
            System.err.println("register error,userName=" + userName);
            e.printStackTrace(); //打印异常栈
            throw new BizException(BizError.INTERNAL_EXCEPTION, "Registration failed,Please contact the site administrator"); //告知用户服务器内部错误
        }
    }

    /**
     * 登录
     * @param userName 用户名
     * @param password 密码
     * @return 成功返回true 失败返回false
     */
    public boolean login(String userName, String password) {
        checkParam(userName, password); //校验参数
        try {
            UserEntity userEntity = userDao.queryByUserNameAndPassword(userName, password);//调用dao查询数据库
            return null != userEntity; //查询到对象就返回登录成功
        } catch (Exception e) {
            System.err.println("login error,userName=" + userName);
            e.printStackTrace(); //打印异常栈
            throw new BizException(BizError.INTERNAL_EXCEPTION, "Login failed,Please contact the site administrator"); //告知用户服务器内部错误
        }
    }

    /**
     * 检查请求参数
     * @param userName 用户名
     * @param password 密码
     */
    private void checkParam(String userName, String password) {
        if (null == userName || userName.trim().isEmpty()) {
            throw new BizException(BizError.INVALID_PARAM, "invalid user name");
        }
        if (null == password || password.trim().isEmpty()) {
            throw new BizException(BizError.INVALID_PARAM, "invalid password");
        }
    }
}
