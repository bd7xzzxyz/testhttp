package com.example.util;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test_http";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345";
    private static final BlockingQueue<Connection> ACTIVE_CONNECTIONS = new ArrayBlockingQueue<>(10);

    static {
        try {
            for (int i = 0; i < 10; i++) {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                ACTIVE_CONNECTIONS.offer(conn);
            }
        } catch (Exception e) {
            System.out.println("cannot get connection, system exit");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static int executeUpdate(String sql, Consumer<PreparedStatement> consumer) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            consumer.accept(ps);
            return ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                returnConnection(connection);
            }
        }
    }

    public static void executeQuery(String sql, Consumer<PreparedStatement> psConsumer, Consumer<ResultSet> resultSetConsumer) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            psConsumer.accept(ps);
            resultSet = ps.executeQuery();
            resultSetConsumer.accept(resultSet);
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                returnConnection(connection);
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        try {
            Connection connection = ACTIVE_CONNECTIONS.poll(500, TimeUnit.MILLISECONDS);
            if (connection == null || !connection.isValid(2)) {
                throw new SQLException("Failed to obtain a valid connection from the pool.");
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Thread was interrupted while waiting for connection.", e);
        }
    }

    private static void returnConnection(Connection connection) {
        try {
            if (!connection.isClosed()) {
                ACTIVE_CONNECTIONS.offer(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
