package com.example.exception;

/**
 * 封装已知运行时异常
 */
public class BizException extends RuntimeException {

    private String message;
    private int errorCode;

    public BizException() {
    }

    /**
     * 包装传递异常
     * @param cause
     */
    public BizException(Throwable cause) {
        super(cause);
    }

    /**
     * 采用默认异常状态码和异常信息构造异常
     * @param bizError 异常状态
     */
    public BizException(BizError bizError) {
        this.errorCode = bizError.getCode();
        this.message = bizError.getMessage();
    }

    /**
     * 采用默认异常状态码和自定义异常信息
     * @param bizError 异常状态
     */
    public BizException(BizError bizError, String message) {
        this.message = message;
        this.errorCode = bizError.getCode();
    }

    /**
     * 获取异常信息
     * @return 异常信息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 获取异常状态码
     * @return 状态码
     */
    public int getErrorCode() {
        return errorCode;
    }
}
