package com.frankzhou.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
