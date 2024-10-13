package com.example.exception;

/**
 * 封装已知异常
 */
public enum BizError {


    INTERNAL_EXCEPTION(500, "Server Internal Error,Please try again."), //服务器内部异常
    INVALID_PARAM(400, "The requested resource was not found."), //无效的请求参数
    NOT_SUPPORT(400, "Bad request."); //不支持的请求

    /**
     * http response 状态码
     */
    private final int code;
    /**
     * http response body
     */
    private final String message;

    /**
     * 定义状态码和异常信息
     * @param code http状态码
     * @param message http异常信息
     */
    BizError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     * @return http状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取异常信息
     * @return http异常信息
     */
    public String getMessage() {
        return message;
    }
}
