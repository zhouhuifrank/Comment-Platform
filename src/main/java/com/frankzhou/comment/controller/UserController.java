package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.dto.LoginDTO;
import com.frankzhou.comment.dto.RegisterDTO;
import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.entity.User;
import com.frankzhou.comment.entity.UserInfo;
import com.frankzhou.comment.service.IUserInfoService;
import com.frankzhou.comment.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理: 登录、验证码、注销、用户校验、返回用户信息
 * @date 2023-01-14
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    @PostMapping("/code")
    public ResultDTO<Boolean> sendCode(@RequestParam("phone") String phone, HttpSession session) {
        return userService.sendCode(phone,session);
    }

    @PostMapping("/login")
    public ResultDTO<String> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return userService.login(loginDTO,session);
    }

    @PostMapping("/loginByPassword")
    public ResultDTO<String> loginByPassword(@RequestBody LoginDTO loginDTO, HttpSession session) {
        return userService.loginByPassword(loginDTO,session);
    }

    @PostMapping("/register")
    public ResultDTO<Boolean> register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @PostMapping("/forgetPassword")
    public ResultDTO<Boolean> forgetPassword(@RequestBody RegisterDTO registerDTO) {
        return userService.forgetPassword(registerDTO);
    }

    @PostMapping("/logout")
    public ResultDTO<Boolean> logout() {
        return ResultDTO.getErrorResult(ErrorResultConstants.FUNCTION_NO_DEV);
    }

    @GetMapping("/me")
    public ResultDTO<UserDTO> getMe() {
        return userService.getMe();
    }

    @GetMapping("/info/{id}")
    public ResultDTO<UserInfo> getUserInfo(@PathVariable("id") Long userId) {
        return userInfoService.getUserInfoById(userId);
    }

    @GetMapping("/{id}")
    public ResultDTO<User> getUserById(@PathVariable("id") Long userId) {
        return userService.getUserById(userId);
    }


}
