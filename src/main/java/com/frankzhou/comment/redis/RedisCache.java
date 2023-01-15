package com.frankzhou.comment.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 缓存工具类 设置逻辑过期时间 解决缓存击穿问题
 * @date 2023-01-14
 */
@Slf4j
@Component
public class RedisCache {

    private StringRedisTemplate stringRedisTemplate;

    private final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    public RedisCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


}
