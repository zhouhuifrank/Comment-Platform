package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.dto.LoginDTO;
import com.frankzhou.comment.dto.RegisterDTO;
import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.entity.User;
import com.frankzhou.comment.entity.UserInfo;

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
     * @return ResultDTO
     */
    ResultDTO<Boolean> sendCode(String phone);

    /**
     * 用户登录 手机号+验证码登录
     *
     * @author this.FrankZhou
     * @param loginDTO 登录请求参数
     * @return token 返回生成的token
     */
    ResultDTO<String> login(LoginDTO loginDTO);

    /**
     * 用户登录 手机号+密码登录
     *
     * @author this.FrankZhou
     * @param loginDTO 登录请求参数
     * @return token 返回生成的token
     */
    ResultDTO<String> loginByPassword(LoginDTO loginDTO);

    /**
     * 用户注册
     *
     * @author this.FrankZhou
     * @param registerDTO 注册请求参数
     * @return Boolean true->修改成功/false->修改失败
     */
    ResultDTO<Boolean> register(RegisterDTO registerDTO);

    /**
     * 忘记密码 修改密码
     *
     * @author this.FrankZhou
     * @param registerDTO 重置密码请求参数
     * @return Boolean true->修改成功/false->修改失败
     */
    ResultDTO<Boolean> forgetPassword(RegisterDTO registerDTO);

    /**
     * 更新用户信息
     *
     * @author this.FrankZhou
     * @param user 修改后的用户信息
     * @return Boolean true->修改成功/false->修改失败
     */
    ResultDTO<Boolean> updateUser(User user);

    /**
     * 获取用户的个人信息
     *
     * @author this.FrankZhou
     * @return UserDTO 用户参数 存在ThreadLocal中
     */
    ResultDTO<UserDTO> getMe();


    /**
     * 通过userId来获取用户的登录细腻些
     *
     * @author this.FrankZhou
     * @param userId 用户编号
     * @return User 用户登录信息
     */
    ResultDTO<User> getUserById(Long userId);
}
