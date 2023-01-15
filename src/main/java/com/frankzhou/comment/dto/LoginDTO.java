package com.frankzhou.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 登录请求参数：验证码登录/手机号登录
 * @date 2023-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO implements Serializable {
    private static final Long serialVersionUID = 83971283L;

    private String phone;

    private String code;

    private String password;
}
