package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.dto.LoginDTO;
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
}
