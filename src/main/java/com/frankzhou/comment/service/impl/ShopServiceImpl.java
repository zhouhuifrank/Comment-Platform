package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.entity.Shop;
import com.frankzhou.comment.mapper.ShopMapper;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺信息服务实现类
 * @date 2023-01-15
 */
@Slf4j
@Service
public class ShopServiceImpl implements IShopService {

    @Resource
    private ShopMapper shopMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultDTO<Shop> getShopByIdHash(Long id) {
        if (Objects.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在查询店铺[{}]的信息",id);
        // 查询redis缓存
        String hashKey = RedisKeys.CACHE_SHOP_KEY + id;
        Map<Object, Object> shopMap = stringRedisTemplate.opsForHash().entries(hashKey);
        if (!Objects.isNull(shopMap)) {
            // 缓存存在直接返回
            Shop shop = BeanUtil.fillBeanWithMap(shopMap, new Shop(), false);
            log.info("店铺[{}]缓存命中,店铺信息:{}",id, JSONUtil.toJsonStr(shopMap));
            return ResultDTO.getSuccessResult(shop);
        }

        // 缓存不存在，查询数据库
        Shop dbShop = shopMapper.selectById(id);
        if (Objects.isNull(dbShop)) {
            // TODO 存在缓存击穿问题
            log.info("店铺[{}]不存在",id);
            return ResultDTO.getErrorResult("店铺"+id+"不存在");
        }
        // 转换成map存入redis TODO 可以替换为工具类
        Map<String, Object> redisObject = BeanUtil.beanToMap(dbShop, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((key, value) -> value.toString()));
        stringRedisTemplate.opsForHash().putAll(hashKey,redisObject);

        return ResultDTO.getSuccessResult(dbShop);
    }

    @Override
    public ResultDTO<Shop> getShopByIdString(Long id) {
        if (Objects.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在查询商铺[{}]的数据",id);

        String shopKey = RedisKeys.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        // 缓存命中
        Shop shop = null;
        if (!Objects.isNull(shopJson)) {
            log.info("商铺[{}]缓存命中,店铺信息:{}",id,shopJson);
            shop = BeanUtil.toBean(shopJson, Shop.class);
            return ResultDTO.getSuccessResult(shop);
        }

        // 缓存不命中
        try {
            LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Shop::getId,id);
            shop = shopMapper.selectOne(wrapper);
            if (Objects.isNull(shop)) {
                log.info("店铺[{}]不存在",id);
                return ResultDTO.getErrorResult("店铺"+id+"不存在");
            }
        } catch (Exception e) {
            log.info("数据库操作异常,{}",e.getMessage());
            // TODO 全局异常处理
        }
        stringRedisTemplate.opsForValue().set(shopKey,JSONUtil.toJsonStr(shop));

        return ResultDTO.getSuccessResult(shop);
    }

    @Override
    public ResultDTO<Shop> getShopWithPenetrate(Long id) {
        return null;
    }

    @Override
    public ResultDTO<Shop> getShopWithBreakDown(Long id) {
        return null;
    }
}
