package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.entity.Shop;
import com.frankzhou.comment.mapper.ShopMapper;
import com.frankzhou.comment.redis.RedisData;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Redis缓存服务实现类
 * @date 2023-07-30
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Resource
    private ShopMapper shopMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final ExecutorService threadExecutor = Executors.newFixedThreadPool(10);


    @Override
    public ResultDTO<Long> saveShop(Shop shop) {
        if (ObjectUtil.isNull(shop)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        Long shopId = shop.getId();
        if (ObjectUtil.isNull(shopId)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在保存[{}]店铺信息", shopId);

        try {
            int insertCount = shopMapper.insert(shop);
            if (insertCount < 1) {
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
            }
        } catch (Exception e) {
            log.error("数据库操作异常");
        }

        return ResultDTO.getSuccessResult(shop.getId());
    }

    @Override
    public ResultDTO<Boolean> updateShop(Shop shop) {
        if (ObjectUtil.isNull(shop)) {
            return ResultDTO.getErrorResult("参数为空");
        }

        Long shopId = shop.getId();
        if (ObjectUtil.isNull(shopId)) {
            return ResultDTO.getErrorResult("店铺id为空");
        }
        log.info("正在更新[{}]店铺信息", shopId);

        Shop oldShop = shopMapper.selectById(shopId);
        if (ObjectUtil.isNull(oldShop)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }

        try {
            // 先更新数据库
            int updateCount = shopMapper.updateById(shop);
            if (updateCount < 1) {
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
            }
            // 删除缓存
            String cacheKey = RedisKeys.CACHE_SHOP_KEY + shopId;
            stringRedisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("数据库操作异常");
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Shop> queryShopWithCacheAside(Long id) {
        if (ObjectUtil.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        String cacheKey = RedisKeys.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(shopJson)) {
            // 缓存存在
            Shop shop = BeanUtil.toBean(shopJson, Shop.class);
            return ResultDTO.getSuccessResult(shop);
        }

        // 缓存不存在
        Shop dbShop = shopMapper.selectById(id);
        if (ObjectUtil.isNull(dbShop)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }
        stringRedisTemplate.opsForValue()
                .set(cacheKey, JSONUtil.toJsonStr(dbShop),RedisKeys.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return ResultDTO.getSuccessResult(dbShop);
    }

    @Override
    public ResultDTO<Shop> queryShopWithPenetrate(Long id) {
        if (ObjectUtil.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        String cacheKey = RedisKeys.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(shopJson)) {
            // 缓存存在
            Shop shop = BeanUtil.toBean(shopJson, Shop.class);
            return ResultDTO.getSuccessResult(shop);
        }

        // 判断是否为空对象，空对象说明数据库中数据不存在，直接返回错误信息
        if ("".equals(shopJson)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }

        // 查询数据库
        Shop dbShop = shopMapper.selectById(id);
        if (ObjectUtil.isNull(dbShop)) {
            stringRedisTemplate.opsForValue()
                    .set(cacheKey,"",RedisKeys.CACHE_NULL_TTL,TimeUnit.MINUTES);
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }
        stringRedisTemplate.opsForValue().set(cacheKey,JSONUtil.toJsonStr(dbShop),RedisKeys.CACHE_SHOP_TTL,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(dbShop);
    }

    @Override
    public ResultDTO<Shop> queryShopWithMutex(Long id) {
        if (ObjectUtil.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        String cacheKey = RedisKeys.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isNotBlank(shopJson)) {
            // 缓存存在
            Shop shop = BeanUtil.toBean(shopJson, Shop.class);
            return ResultDTO.getSuccessResult(shop);
        }

        // 判断命中的缓存是否是空字符串
        if ("".equals(shopJson)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }

        // 缓存重建 获取锁
        Shop dbShop = null;
        String lockKey = RedisKeys.LOCK_SHOP_LEY + id;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isSuccess = lock.tryLock(RedisKeys.LOCK_SHOP_TTL, TimeUnit.SECONDS);
            // 获取锁成功后进行doubleCheck，看缓存中是否已经有数据了
            String doubleCheckCache = stringRedisTemplate.opsForValue().get(cacheKey);
            // 如果有了，那么就不需要进行缓存重建了，直接返回新数据
            if (StrUtil.isNotBlank(doubleCheckCache)) {
                return ResultDTO.getSuccessResult(BeanUtil.toBean(doubleCheckCache,Shop.class));
            }

            if (!isSuccess) {
                // 获取锁失败，休眠后进行重试
                Thread.sleep(2000);
                return this.queryShopWithMutex(id);
            }
            // 获取锁成功，缓存重建
            // 数据库数据不存在
            dbShop = shopMapper.selectById(id);
            if (ObjectUtil.isNull(dbShop)) {
                // 将空值写入redis，防止缓存穿透
                stringRedisTemplate.opsForValue().set(cacheKey,"",RedisKeys.CACHE_NULL_TTL,TimeUnit.MINUTES);
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
            }
            stringRedisTemplate.opsForValue().set(cacheKey,JSONUtil.toJsonStr(dbShop), RedisKeys.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return ResultDTO.getSuccessResult(dbShop);
    }

    @Override
    public ResultDTO<Shop> queryShopWithLogicTime(Long id) {
        if (ObjectUtil.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        String cacheKey = RedisKeys.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        // 采用逻辑时间，缓存永不过期 如果没有缓存直接返回
        if (StrUtil.isBlank(shopJson)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }
        // 缓存存在，判断过期时间
        RedisData redisData = BeanUtil.toBean(shopJson, RedisData.class);
        Shop cacheShop = BeanUtil.toBean((JSONObject) redisData.getData(),Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 没有过期,直接返回结果
            return ResultDTO.getSuccessResult(cacheShop);
        }
        // 缓存逻辑过期了，需要缓存重建
        String lockKey = RedisKeys.LOCK_SHOP_LEY + id;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isSuccess = lock.tryLock(RedisKeys.LOCK_SHOP_TTL, TimeUnit.SECONDS);
            // 获取锁成功后进行doubleCheck，看缓存中是否已经有数据了
            String doubleCheckCache = stringRedisTemplate.opsForValue().get(cacheKey);
            // 如果有了，那么就不需要进行缓存重建了，直接返回新数据
            if (StrUtil.isNotBlank(doubleCheckCache)) {
                return ResultDTO.getSuccessResult(BeanUtil.toBean(doubleCheckCache,Shop.class));
            }
            if (!isSuccess) {
                // 获取锁失败, 直接返回旧数据
                return ResultDTO.getSuccessResult(cacheShop);
            }

            // 获取锁成功，开启异步线程去进行缓存重建
            threadExecutor.submit(() -> {
                Shop dbShop = shopMapper.selectById(id);
                if (ObjectUtil.isNull(dbShop)) {
                    return;
                }

                RedisData logicExpireData = RedisData.builder()
                        .data(dbShop)
                        .expireTime(LocalDateTime.now().plusSeconds(TimeUnit.MINUTES.toSeconds(RedisKeys.CACHE_SHOP_TTL)))
                        .build();
                stringRedisTemplate.opsForValue()
                        .set(cacheKey,JSONUtil.toJsonStr(logicExpireData),RedisKeys.CACHE_SHOP_TTL,TimeUnit.MINUTES);
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        // 缓存重建由异步线程进行，主线程直接返回旧数据
        return ResultDTO.getSuccessResult(cacheShop);
    }
}
