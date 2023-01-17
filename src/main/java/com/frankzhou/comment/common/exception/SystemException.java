package com.frankzhou.comment.common.exception;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 系统异常
 * @date 2023-01-17
 */
public class SystemException extends RuntimeException {
    private static final Long serialVersionUID = 793821L;

    private Integer code;

    public SystemException(String message,Throwable throwable) {
        super(message,throwable);
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
