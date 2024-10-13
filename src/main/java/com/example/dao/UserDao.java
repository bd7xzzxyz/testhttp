package com.example.dao;

import com.example.entity.UserEntity;
import com.example.exception.BizException;
import com.example.util.DBUtil;

import java.sql.SQLException;

/**
 * 用户dao
 */
public class UserDao {
    private static final UserDao INSTANCE = new UserDao(); //单例对象

    private UserDao() {
    }

    /**
     * 获取UserDao实例
     * @return
     */
    public static UserDao getInstance() {
        return INSTANCE; //返回实例
    }

    /**
     * 添加用户
     * @param userName 用户名
     * @param password 密码
     */
    public void addUser(String userName, String password) {
        String query = "INSERT INTO t_users (username, password) VALUES (?, ?)";
        try {
            DBUtil.executeUpdate(query, preparedStatement -> { //执行入库
                try {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, password);
                } catch (SQLException e) {
                    throw new BizException(e); //dao不做任何处理向上传递异常
                }
            });
        } catch (SQLException e) {
            throw new BizException(e); //dao不做任何处理向上传递异常
        }
    }

    /**
     * 通过用户名和密码查询用户实体
     * @param userName 用户名
     * @param password 密码
     * @return 用户实体对象
     */
    public UserEntity queryByUserNameAndPassword(String userName, String password) {
        String query = "SELECT * FROM t_users WHERE username = ? AND password = ? LIMIT 1";
        try {

            return DBUtil.executeQuery(query, preparedStatement -> {
                try {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, password);
                } catch (SQLException e) {
                    throw new BizException(e); //dao不做任何处理向上传递异常
                }
            }, resultSet -> {
                try {
                    if (resultSet.next()) { //结果转换成对象
                        return UserEntity.builder()
                                .id(resultSet.getLong("id"))
                                .username(resultSet.getString("username"))
                                .password(resultSet.getString("password"))
                                .build();
                    }
                } catch (SQLException e) {
                    throw new BizException(e); //dao不做任何处理向上传递异常
                }
                return null;
            });
        } catch (SQLException e) {
            throw new BizException(e); //dao不做任何处理向上传递异常
        }
    }
}
