package com.frankzhou.comment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 点评系统主启动类
 * @date 2023-01-14
 */
@SpringBootApplication
@MapperScan(basePackages = "com.frankzhou.comment.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableTransactionManagement
public class CommentSystemApplication {

    public static void main(String[] args) {
        System.out.println("Redis启动");
        System.out.println("Mybatis启动");
        System.out.println("Druid数据源连接池启动");
        SpringApplication.run(CommentSystemApplication.class, args);
        System.out.println("--------------------------------------");
        System.out.println("-------------Start Success------------");
        System.out.println("--------------------------------------");
    }

}
