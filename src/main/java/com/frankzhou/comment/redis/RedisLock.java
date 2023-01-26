package com.frankzhou.comment.redis;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Redis分布式锁基础版
 * @date 2023-01-26
 */
public interface RedisLock {

    /**
     * 尝试获取锁
     *
     * @author this.FrankZhou
     * @param key 分布式锁的key
     * @param timeSec 锁的有效时间
     * @return boolean true->获取锁成功/false->获取锁失败
     */
    boolean tryLock(String key,Long timeSec);

    /**
     * 释放分布式锁
     *
     * @author this.FrankZhou
     * @param key 分布式锁的key
     * @return void
     */
    void unlock(String key);
}
