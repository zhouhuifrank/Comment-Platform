package com.frankzhou.comment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.frankzhou.comment.entity.ShopType;
import com.frankzhou.comment.mapper.ShopTypeMapper;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.IShopTypeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-16
 */
@Slf4j
@SpringBootTest
public class ShopTypeTests {

    @Resource
    private ShopTypeMapper shopTypeMapper;

    @Resource
    private IShopTypeService shopTypeService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testQueryAllShopTypeList() {
        List<ShopType> shopTypeList = shopTypeMapper.queryAllShopTypeList();
        shopTypeList.stream().forEach(System.out::println);
    }

    @Test
    public void testListToString() {
        // 将List中每一个元素都序列化
        List<ShopType> shopTypeList = shopTypeMapper.queryAllShopTypeList();
        List<String> redisList = shopTypeList.stream().map(item -> {
            String shopJson = JSONUtil.toJsonStr(item);
            return shopJson;
        }).collect(Collectors.toList());
        redisList.stream().forEach(System.out::println);

        String listKey = RedisKeys.CACHE_SHOP_TYPE_KEY;
        /*
        redisList.forEach(item -> {
            stringRedisTemplate.opsForList().leftPush(listKey,item);
        });
        */
        stringRedisTemplate.opsForList().leftPushAll(listKey,redisList);

        log.info("redis缓存成功!");
    }
}
