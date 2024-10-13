package com.example.util;

import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 数据库链接工具类
 */
public class DBUtil {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/test_http"; //jdbc连接
    private static final String DB_USER = "root"; //数据库连接用户名
    private static final String DB_PASSWORD = "12345"; //数据库连接密码
    private static final int POOL_MAX = 10;//初始化10个连接入池
    private static final BlockingQueue<Connection> ACTIVE_CONNECTIONS = new ArrayBlockingQueue<>(10); //连接池

    private DBUtil() {
    }

    static {
        try { //初始化连接池
            for (int i = 0; i < POOL_MAX; i++) {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); //获取jdbc连接
                if (!ACTIVE_CONNECTIONS.offer(conn)) { //放入连接池
                    System.err.println("cannot get connection,index:" + i);
                }
            }
        } catch (Exception e) {
            //获取不到连接直接退出系统
            System.err.println("cannot get connection, system exit");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 执行数据库增删改
     * @param sql 增删改SQL语句
     * @param consumer 回调函数，通过PreparedStatement设置SQL参数
     * @return 执行成功的条数
     * @throws SQLException jdbc SQL异常
     */
    public static int executeUpdate(String sql, Consumer<PreparedStatement> consumer) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = getConnection(); //连接池获取一个连接
            ps = connection.prepareStatement(sql); //构造PreparedStatement
            consumer.accept(ps); //填充SQL参数
            return ps.executeUpdate(); //执行SQL
        } finally {
            if (ps != null) { //关闭 PreparedStatement
                ps.close();
            }
            if (connection != null) { //归还连接池
                returnConnection(connection);
            }
        }
    }

    /**
     * 执行查询
     * @param sql 查询SQL语句
     * @param psConsumer  回调函数，通过PreparedStatement设置SQL参数
     * @param resultFunction 查询结果回调函数，实现ORM功能
     * @return ORM实体类对象
     * @param <R> 实体类型
     * @throws SQLException  jdbc SQL异常
     */
    public static <R> R executeQuery(String sql, Consumer<PreparedStatement> psConsumer, Function<ResultSet, R> resultFunction) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection(); //连接池中获取一个连接
            ps = connection.prepareStatement(sql);  //构造PreparedStatement
            psConsumer.accept(ps);  //填充SQL参数
            resultSet = ps.executeQuery(); //执行查询
            return resultFunction.apply(resultSet); //进行ORM
        } finally {
            if (resultSet != null) { //关闭ResultSet
                resultSet.close();
            }
            if (ps != null) { //关闭 PreparedStatement
                ps.close();
            }
            if (connection != null) { //归还连接池
                returnConnection(connection);
            }
        }
    }

    /**
     * 连接池中获取连接
     * @return 获取的jdbc连接Connection对象
     * @throws SQLException  jdbc SQL异常
     */
    private static Connection getConnection() throws SQLException {
        try {
            Connection connection = ACTIVE_CONNECTIONS.poll(500, TimeUnit.MILLISECONDS); //等待500秒获取不到连接直接抛出异常
            if (connection == null || !connection.isValid(2)) {//校验获取的连接是否有效
                throw new SQLException("Failed to obtain a valid connection from the pool.");
            }
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); //标记interrupted标记位
            throw new SQLException("Thread was interrupted while waiting for connection.", e);
        }
    }

    /**
     * 连接归还连接池
     * @param connection
     */
    private static void returnConnection(Connection connection) {
        try {
            if (!connection.isClosed()) { //连接如果没有被关闭，可进行复用放回连接池
                if (!ACTIVE_CONNECTIONS.offer(connection)) {
                    System.err.println("connection return failed!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); //归还失败，打印异常信息，不应向上抛出异常影响调用者后续的流程
        }
    }
}
