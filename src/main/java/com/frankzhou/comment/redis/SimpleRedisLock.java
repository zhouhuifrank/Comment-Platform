package com.frankzhou.comment.redis;

import cn.hutool.core.lang.UUID;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分布式锁 使用线程标识解决锁的误删问题 使用lua脚本解决多条语句的原子性问题
 * @date 2023-01-26
 */
public class SimpleRedisLock implements RedisLock {

    private static final String DISTRIBUTED_LOCK = "distribution:key:";

    private static final String NAME = "seckill";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    @Override
    public boolean tryLock(Long timeSec) {
        return false;
    }

    @Override
    public void unlock() {

    }
}
