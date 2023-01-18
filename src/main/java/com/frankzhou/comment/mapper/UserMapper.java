package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户登录信息mapper sql
 * @date 2023-01-14
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
