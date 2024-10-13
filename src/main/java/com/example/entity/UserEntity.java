package com.example.entity;

/**
 * 用户实体类，与t_users表进行映射
 */
public class UserEntity {

    /**
     * 数据库主键
     */
    private final long id;
    /**
     * 用户名
     */
    private final String username;
    /**
     * 用户密码
     */
    private final String password;

    /**
     * 获取主键
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * 获取用户名
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 获取密码
     * @return password
     */
    public String getPassword() {
        return password;
    }

    private UserEntity(long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * 生成UserEntity构造器对象
     * @return UserEntity对象
     */
    public static UserEntity.Builder builder() {
        return new UserEntity.Builder();
    }

    /**
     *  构造UserEntity对象构造器
     */
    public static class Builder {
        private long id;
        private String username;
        private String password;

        /**
         * 对id赋值
         * @param id
         * @return 构造器
         */
        public Builder id(long id) {
            this.id = id;
            return this;
        }

        /**
         * 对username赋值
         * @param username 用户名
         * @return 构造器
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * 对password赋值
         * @param password 密码
         * @return 构造器
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * 生成UserEntity对象
         * @return UserEntity对象
         */
        public UserEntity build() {
            return new UserEntity(this.id, this.username, this.password);
        }
    }
}
