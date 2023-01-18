package com.frankzhou.comment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.comment.entity.Shop;
import com.frankzhou.comment.mapper.ShopMapper;
import com.frankzhou.comment.redis.RedisData;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-01-16
 */
@Slf4j
@SpringBootTest
public class ShopTests {

    @Resource
    private ShopMapper shopMapper;

    @Resource
    private IShopService shopService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final int HASH_INCREMENT = 0x61c88647;

    private static final int HASH_LENGTH = 32;

    @Test
    public void testRedisShopList() {
        // 预加载商铺数据
        List<Shop> shopList = shopMapper.queryShopList();
        shopList.forEach(System.out::println);

        for (Shop dbShop:shopList) {
            String hashKey = RedisKeys.CACHE_SHOP_KEY + dbShop.getId();
            if (Objects.isNull(dbShop)) continue;
            Map<String, Object> shopMap = BeanUtil.beanToMap(dbShop, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((key, value) -> value.toString()));
            try {
                stringRedisTemplate.opsForHash().putAll(hashKey,shopMap);
            } catch (Exception e) {
                log.info("Redis服务异常");
            }
        }
    }

    @Test
    public void testLoadShopData() {
        Long id = 1L;
        Shop shop = shopMapper.selectById(id);
        log.info("查询到商铺数据[{}]:{}",id, JSONUtil.toJsonStr(shop));

        String hashKey = RedisKeys.CACHE_SHOP_KEY + id;
        Map<String, Object> shopMap = BeanUtil.beanToMap(shop, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((key,value) -> value.toString()));
        System.out.println(shopMap);
        stringRedisTemplate.opsForHash().putAll(hashKey,shopMap);
        log.info("redis缓存商铺数据成功");
    }

    @Test
    public void testSetLogicTime() {
        String shopKey = RedisKeys.CACHE_SHOP_KEY + 1L;
        Shop shop = shopMapper.selectById(1);
        // shopService.setLogicTime(shopKey,shop,10L, TimeUnit.MINUTES);
    }

    @Test void testGetLogicTime() {
        String shopKey = RedisKeys.CACHE_SHOP_KEY + 1L;
        String result = stringRedisTemplate.opsForValue().get(shopKey);
        RedisData data = JSONUtil.toBean(result,RedisData.class);
        System.out.println(data);
        LocalDateTime time = data.getExpireTime();
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(time)) {
            System.out.println("缓存已过期");
        } else {
            System.out.println("缓存未过期");
        }
    }

    @Test
    public void testFibHash() {
        int fibHash,remainderHash;
        for (int i=0;i<HASH_LENGTH;i++) {
            fibHash = (i*HASH_INCREMENT+HASH_INCREMENT) & (HASH_LENGTH-1);
            remainderHash = i % (HASH_LENGTH-1);
            System.out.println("斐波那契散列:"+fibHash+" 普通散列:"+remainderHash);
        }
    }

    @Test
    public void testRandomUtils() {
        long baseTime = 20;
        for (int i=0;i<100;i++) {
            long time = (long) RandomUtil.randomInt(5,10);
            long expireTime = baseTime+ time;
            System.out.println(expireTime);
        }
    }
}
