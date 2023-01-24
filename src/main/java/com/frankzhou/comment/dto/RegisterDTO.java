package com.frankzhou.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 注册请求参数DTO
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 72718313L;

    private String phone;

    private String password;

    private String newPassword;

    private String nickName;
}
