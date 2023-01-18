package com.frankzhou.comment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.frankzhou.comment.entity.User;
import com.frankzhou.comment.mapper.UserMapper;
import com.frankzhou.comment.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-15
 */
@SpringBootTest
public class UserTests {

   @Resource
   private UserMapper userMapper;

   @Test
   public void testUserDO() {
       User user = new User();
       user.setNickName("zhouzhou");
       user.setPhone("13818904582");
       user.setPassword("monv205850");
       userMapper.insert(user);
   }
}
