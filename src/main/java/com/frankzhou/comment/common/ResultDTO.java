package com.frankzhou.comment.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 前端通用返回类
 * @date 2023-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO<T> implements Serializable {
    private static final Long serialVersionUID = 2173817L;

    private T data;

    private Boolean success;

    private String errorMsg;

    private Long total;

    public static <T> ResultDTO<T> getSuccessResult() {
        ResultDTO<T> result = new ResultDTO<>(null,Boolean.TRUE,null,null);
        return result;
    }

    public static <T> ResultDTO<T> getSuccessResult(T data) {
        ResultDTO<T> result = new ResultDTO<>(data,Boolean.TRUE,null,null);
        return result;
    }

    public static <T> ResultDTO<List<T>> getPageSuccessResult(List<T> data, Long total) {
        ResultDTO<List<T>> result = new ResultDTO<>(data,Boolean.TRUE,null,total);
        return result;
    }

    public static <T> ResultDTO<T> getErrorResult(String message) {
        ResultDTO<T> result = new ResultDTO<>(null,Boolean.FALSE,message,null);
        return result;
    }
}
