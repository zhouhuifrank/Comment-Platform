package com.frankzhou.comment.service.impl;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.UserInfo;
import com.frankzhou.comment.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户信息接口实现类
 * @date 2023-01-14
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements IUserInfoService {
    @Override
    public ResultDTO<UserInfo> getUserInfoById(Long userId) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> updateUserInfo(UserInfo userInfo) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> deleteUserInfo(Long userId) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> addUserInfo(UserInfo userInfo) {
        return null;
    }
}
