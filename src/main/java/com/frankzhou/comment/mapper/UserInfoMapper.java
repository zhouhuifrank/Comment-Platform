package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户详细信息mapper sql
 * @date 2023-01-14
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
