package com.example.util;

import org.omg.CORBA.UNSUPPORTED_POLICY;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test_http";  //数据库的连接地址
    private static final String DB_USER = "root";   //数据库用户名
    private static final String DB_PASSWORD = "12345";   //数据库密码
    private static final BlockingQueue<Connection> ACTIVE_CONNECTIONS = new ArrayBlockingQueue<>(10);
    private static final BlockingQueue<Connection> USING_CONNECTIONS = new ArrayBlockingQueue<>(10);

    static {
        try {
            for (int i = 0; i < 10; i++) {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                ACTIVE_CONNECTIONS.offer(conn);
            }
        } catch (Exception e) {
            System.out.println("cannot get connection,system exit");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static int executeUpdate(String sql, Consumer<PreparedStatement> consumer) throws SQLException {
        PreparedStatement ps = null;
        Connection connection = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            consumer.accept(ps);
            return ps.executeUpdate();
        } finally {
            if (null != ps) {
                ps.close();
            }
            if (null != connection) {
                ACTIVE_CONNECTIONS.offer(connection);
                USING_CONNECTIONS.remove(connection);
            }
        }
    }

    public static void executeQuery(String sql, Consumer<PreparedStatement> psConsumer, Consumer<ResultSet> resultSetConsumer) throws SQLException {
        PreparedStatement ps = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            psConsumer.accept(ps);
            resultSet = ps.executeQuery();
            resultSetConsumer.accept(resultSet);
        } finally {
            if (null != ps) {
                ps.close();
            }
            if (null != resultSet) {
                resultSet.close();
            }
            if (null != connection) {
                ACTIVE_CONNECTIONS.offer(connection);
                USING_CONNECTIONS.remove(connection);
            }
        }
    }

    private static Connection getConnection() {
        while (true) {
            Connection connection = ACTIVE_CONNECTIONS.poll();
            if (connection == null) {
                try {
                    Thread.currentThread().wait(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                USING_CONNECTIONS.offer(connection);
                return connection;
            }
        }
    }


}
