package com.frankzhou.comment;

import com.frankzhou.comment.redis.RedisCache;
import com.frankzhou.comment.redis.RedisIdGenerator;
import com.frankzhou.comment.redis.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-18
 */
@SpringBootTest
public class IdGeneratorTests {

    @Resource
    private RedisIdGenerator redisIdGenerator;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RedisCache redisCache;

    private static final ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    public void testIdGenerator() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);

        // 使用lambda表达式来编写异步执行的任务
        Runnable task = () -> {
            for (int i=0;i<100;i++) {
                System.out.println(i);
            }
            latch.countDown();
        };

        long start = System.currentTimeMillis();
        for (int i=0;i<300;i++) {
            es.submit(task);
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("Id生成总用时:" + (end-start));
    }
}
