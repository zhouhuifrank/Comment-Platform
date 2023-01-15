package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.UserInfo;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户信息服务接口
 * @date 2023-01-14
 */
public interface IUserInfoService {

    /**
     * 通过userId来获取用户的详细信息
     *
     * @param userId 用户编号 user_info表主键
     * @return UserInfo 用户详细信息
     * @author this.FrankZhou
     */
    ResultDTO<UserInfo> getUserInfoById(Long userId);

    /**
     * 更新UserInfo表中的用户信息
     *
     * @author this.FrankZhou
     * @param userInfo 用户详细信息
     * @return Boolean true->修改成功/false->修改失败
     */
    ResultDTO<Boolean> updateUserInfo(UserInfo userInfo);

    /**
     * 根据id删除用户 逻辑删除 添加status字段
     *
     * @author this.FrankZhou
     * @param userId 用户编号
     * @return Boolean true->修改成功/false->修改失败
     */
    ResultDTO<Boolean> deleteUserInfo(Long userId);

    /**
     * 添加用户信息
     *
     * @author this.FrankZhou
     * @param userInfo 用户信息实体类
     * @return Boolean true->修改成功/false->修改失败
     */
    ResultDTO<Boolean> addUserInfo(UserInfo userInfo);
}