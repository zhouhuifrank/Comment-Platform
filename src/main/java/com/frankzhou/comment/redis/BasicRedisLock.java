package com.frankzhou.comment.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-26
 */
public class BasicRedisLock implements RedisLock {

    private static final String DISTRIBUTED_LOCK = "distribution:key:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean tryLock(String key, Long timeSec) {
        return false;
    }

    @Override
    public void unlock(String key) {

    }
}
