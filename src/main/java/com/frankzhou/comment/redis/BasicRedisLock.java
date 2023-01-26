package com.frankzhou.comment.redis;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分布式锁初级版本 使用线程标识来防止锁的误删
 * @date 2023-01-26
 */
public class BasicRedisLock implements RedisLock {

    private static final String DISTRIBUTED_LOCK = "distribution:key:";

    private static final String NAME = "seckill";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean tryLock(Long timeSec) {
        // 存入线程标识号
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(DISTRIBUTED_LOCK + NAME, threadId, timeSec, TimeUnit.SECONDS);

        return BooleanUtil.isTrue(lock);
    }

    @Override
    public void unlock() {
        // 获取自己的线程标识
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        // 获取锁中的线程标识
        String lock = stringRedisTemplate.opsForValue().get(DISTRIBUTED_LOCK + NAME);
        // 比较线程标识是否一致
        if (lock.equals(threadId)) {
            stringRedisTemplate.delete(DISTRIBUTED_LOCK+NAME);
        }
    }
}
