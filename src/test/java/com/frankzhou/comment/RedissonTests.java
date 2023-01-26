package com.frankzhou.comment;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-19
 */
@SpringBootTest
public class RedissonTests {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void testLock() throws InterruptedException {
        RLock lock = redissonClient.getLock("anyLock");
        boolean isLock = lock.tryLock(1, 10, TimeUnit.SECONDS);
        if (isLock) {
            System.out.println("获取分布式锁成功");
            try {
                System.out.println("执行相关业务逻辑");
            } finally {
                lock.unlock();
                System.out.println("分布式锁释放");
            }
        } else {
            System.out.println("获取分布式锁失败");
        }
    }

    @Test
    public void testUnLock() throws InterruptedException {
        RLock lock = redissonClient.getLock("anyLock");
        boolean isLock = lock.tryLock(1,10,TimeUnit.SECONDS);
        if (isLock) {
            System.out.println("获取分布式锁成功");
        } else {
            System.out.println("获取分布式锁失败");
        }
    }

}
