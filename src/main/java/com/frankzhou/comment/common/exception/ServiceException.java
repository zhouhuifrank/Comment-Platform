package com.frankzhou.comment.common.exception;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 服务异常
 * @date 2023-01-17
 */
public class ServiceException extends RuntimeException {
    private static final Long serialVersionUID = 27391882L;

    private Integer code;

    public ServiceException(String message,Throwable throwable) {
        super(message,throwable);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
