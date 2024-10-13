package com.example.dao;

import com.example.util.DBUtil;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserDao {

    public void addUser(String userName, String password) {
        String query = "INSERT INTO t_users (username, password) VALUES (?, ?)";
        try {
            DBUtil.executeUpdate(query, preparedStatement -> {
                try {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, password);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            System.out.println("addUser error,userName=" + userName);
            e.printStackTrace();
        }
    }

    public boolean login(String userName, String password) {
        String query = "SELECT * FROM t_users WHERE username = ? AND password = ?";
        AtomicBoolean result = new AtomicBoolean(false);
        try {
            DBUtil.executeQuery(query, preparedStatement -> {
                try {
                    preparedStatement.setString(1, userName);
                    preparedStatement.setString(2, password);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, resultSet -> {
                try {
                    String uname = resultSet.getString(1);
                    result.set(null != uname && uname.equals(userName));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            System.out.println("addUser error,userName=" + userName);
            e.printStackTrace();
        }
        return result.get();
    }
}
