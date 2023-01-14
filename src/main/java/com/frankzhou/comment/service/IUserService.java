package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.dto.LoginDTO;

import javax.servlet.http.HttpSession;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理服务接口
 * @date 2023-01-14
 */
public interface IUserService {

    /**
     * 发送验证码
     *
     * @author this.FrankZhou
     * @param phone 手机号
     * @param session 服务端session
     * @return ResultDTO
     */
    ResultDTO<Boolean> sendCode(String phone, HttpSession session);

    /**
     * 用户登录
     *
     * @author this.FrankZhou
     * @param loginDTO 登录请求参数
     * @param session 服务端session
     * @return ResultDTO
     */
    ResultDTO<String> login(LoginDTO loginDTO, HttpSession session);
}
