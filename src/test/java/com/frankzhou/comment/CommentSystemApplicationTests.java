package com.frankzhou.comment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
* @Author this.FrankZhou
* @Description 测试环境配置是否正确
* @DateTime 2023/1/15 15:33
*/
@SpringBootTest
class CommentSystemApplicationTests {

    @Test
    void contextLoads() {
        String notice = "当配置多环境yml文件时，必须在主环境配置application.yml中，配好启动的环境，才能使用单元测试";
        System.out.println(notice);
        System.out.println("加载环境成功!");
    }

}
