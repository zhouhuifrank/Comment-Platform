package com.frankzhou.comment.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 封装StringRedisTemplate对于各种类型数据的操作
 * @date 2023-01-15
 */
@Slf4j
@Component
public class StringRedisUtil {

    private StringRedisTemplate stringRedisTemplate;

    public StringRedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // =================通用redis操作====================



    // =================String类型操作===================



    // =================Hash类型操作======================


    // =================List类型操作======================



    // =================Set类型操作=======================


    // =================SortedSet类型操作=================

}
