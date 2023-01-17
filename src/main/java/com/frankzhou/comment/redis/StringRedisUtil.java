package com.frankzhou.comment.redis;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 封装StringRedisTemplate对于各种类型数据的操作  每个数据包含存入缓存 不带TTL/带TTL 获取缓存
 * @date 2023-01-15
 */
@Slf4j
@Component
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class StringRedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    // =================通用redis操作====================

    private Boolean setExpire(String key, Long time, TimeUnit unit) {
        return stringRedisTemplate.expire(key,time,unit);
    }

    private Boolean setExpire(String key, Long time) {
        return setExpire(key,time,TimeUnit.SECONDS);
    }

    private Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    private Boolean hashKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    private Boolean deleteObject(String key) {
        return stringRedisTemplate.delete(key);
    }

    private Long deleteObject(Collection<String> collection) {
        Long count = stringRedisTemplate.delete(collection);
        return count == null ? 0 : count;
    }

    // =================String类型操作===================

    private <T> void setStringObject(String key,T value) {
        String json = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key,json);
    }

    private <T> void setStringObject(String key,T value,Long time,TimeUnit unit) {
        String json = JSONUtil.toJsonStr(value);
        stringRedisTemplate.opsForValue().set(key,json,time,unit);
    }

    private <T> T getStringObject(String key,Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        T result = BeanUtil.toBean(json, clazz);
        return result;
    }

    // =================Hash类型操作======================

    private <T> void setHashObject(String key,T value) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(value, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((k, v) -> v.toString()));
        stringRedisTemplate.opsForHash().putAll(key,stringObjectMap);
    }

    private <T> void setHashObject(String key,T value,Long time,TimeUnit unit) {
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(value, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((k, v) -> v.toString()));
        stringRedisTemplate.opsForHash().putAll(key,stringObjectMap);
        stringRedisTemplate.expire(key,time,unit);
    }

    private <T> T getHashObject(String key,Class<T> clazz) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        T target = null;
        try {
            target = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        T result = BeanUtil.fillBeanWithMap(entries, target, false);
        return result;
    }

    // =================List类型操作======================

    private <T> void setListObject(String key,List<T> cacheList) {
        List<String> stringList = cacheList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());

    }

    // =================Set类型操作=======================

    private <T> void setSetObject(String key,T value) {
        return;
    }

    private <T> T getSetObject(String key,Class<T> clazz) {
        return null;
    }

    // =================SortedSet类型操作=================

    private <T> void setZSetObject(String key,T value) {
        return;
    }

    private <T> T getZSetObject(String key,Class<T> clazz) {
        return null;
    }

}
