package com.frankzhou.comment.common.exception;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-17
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultDTO<Boolean> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String msg;
        try {
            msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } catch (Exception e) {
            msg = "";
        }
        return ResultDTO.getErrorResult(msg);
    }

    @ExceptionHandler(value = ServiceException.class)
    public ResultDTO<Boolean> serviceException(ServiceException ex) {
        Integer code = ex.getCode();
        if (!Objects.isNull(code)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.FUNCTION_NO_DEV);
        }
        return ResultDTO.getErrorResult(ex.getMessage());
    }

    @ExceptionHandler(value = SystemException.class)
    public ResultDTO<Boolean> systemException(ServiceException ex) {
        Integer code = ex.getCode();
        if (!Objects.isNull(code)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.FUNCTION_NO_DEV);
        }
        return ResultDTO.getErrorResult(ex.getMessage());
    }
}
