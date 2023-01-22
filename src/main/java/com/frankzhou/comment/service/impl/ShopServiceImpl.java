package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.entity.Shop;
import com.frankzhou.comment.mapper.ShopMapper;
import com.frankzhou.comment.redis.RedisCache;
import com.frankzhou.comment.redis.RedisData;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.redis.StringRedisUtil;
import com.frankzhou.comment.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private StringRedisUtil stringRedisUtil;

    @Resource
    private RedisCache redisCache;

    private static final ExecutorService es = Executors.newFixedThreadPool(10);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Long> updateShop(Shop shop) {
        Long id = shop.getId();
        if (Objects.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在更新店铺[{}]的信息",id);
        // 先更新数据库
        Integer updateCount = shopMapper.updateById(shop);
        if (updateCount != 1) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }
        // 再删除缓存，结束数据库-缓存一致性问题 需要保证原子性，缓存操作和数据库操作在同一个事务内
        // 单机情况下，可以直接用spring的声明式事务，分布式情况需要TTC等方法实现分布式事务
        String shopKey = RedisKeys.CACHE_SHOP_KEY + id;
        stringRedisTemplate.delete(shopKey);

        return ResultDTO.getSuccessResult(id);
    }

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
            // TODO 存在缓存穿透问题 解决方案 缓存空值
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
        if (StrUtil.isNotBlank(shopJson)) {
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
                // 在redis中存入空值并设置过期时间10分钟
                stringRedisTemplate.opsForValue().set(shopKey,"",RedisKeys.CACHE_NULL_TTL, TimeUnit.MINUTES);
                return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
            }
        } catch (Exception e) {
            log.info("数据库操作异常,{}",e.getMessage());
        }

        // 设置随机过期时间
        int randomNum = RandomUtil.randomInt(5, 10);
        Long cacheTime = RedisKeys.CACHE_SHOP_TTL + randomNum;
        stringRedisTemplate.opsForValue().set(shopKey,JSONUtil.toJsonStr(shop),cacheTime,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(shop);
    }

    @Override
    public ResultDTO<Shop> getShopWithPenetrate(Long id) {
        if (Objects.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在查询店铺[{}]的信息",id);

        // 先查缓存
        String shopKey = RedisKeys.CACHE_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(shopKey);
        Shop shop = null;
        if (StrUtil.isNotBlank(shopJson)) {
            log.info("店铺[{}]缓存命中，店铺信息:{}",id,shopJson);
            shop = BeanUtil.toBean(shopJson,Shop.class);
            return ResultDTO.getSuccessResult(shop);
        }

        // 上面缓存存在的情况，由于这里为了解决缓存穿透缓存了空字符串，因此需要判断是否为空字符，若是空字符串则直接返回
        if (!Objects.isNull(shopJson)) {
            // 说明是空字符串
            log.info("店铺[{}]信息不存在",id);
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }

        // 查询数据库，重新缓存商铺数据
        try {
            shop = shopMapper.selectById(id);
            if (Objects.isNull(shop)) {
                // 缓存空对象
                stringRedisTemplate.opsForValue().set(shopKey,"",RedisKeys.CACHE_NULL_TTL,TimeUnit.MINUTES);
                return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
            }
        } catch (Exception e) {
            log.warn("数据库操作异常,{}",e.getMessage());
        }

        stringRedisTemplate.opsForValue().set(shopKey,shopJson,RedisKeys.CACHE_SHOP_TTL,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(shop);
    }

    @Override
    public ResultDTO<Shop> getShopWithMutex(Long id) {
        if (Objects.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在查询商铺[{}]的数据",id);

        String shopKey = RedisKeys.CACHE_SHOP_KEY + id;
        String jsonShop = stringRedisTemplate.opsForValue().get(shopKey);
        Shop shop = null;
        if (StrUtil.isNotBlank(jsonShop)) {
            log.info("商铺[{}]缓存命中,商铺信息:{}",id,jsonShop);
            shop = JSONUtil.toBean(jsonShop, Shop.class);
            return ResultDTO.getSuccessResult(shop);
        }

        // 判断是不是空值
        if (jsonShop != null) {
            log.info("商铺[{}]不存在",id);
            return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
        }

        // 缓存不存在，重建缓存
        String lockKey = RedisKeys.MUTEX_LOCK_KEY + id;
        try {
            boolean lock = tryLock(lockKey,10L,TimeUnit.SECONDS);
            if (!lock) {
                // 获取锁失败
                Thread.sleep(10);
                // 递归继续重试
                return getShopWithMutex(id);
            }
            // 获取锁成功
            shop = shopMapper.selectById(id);
            if (Objects.isNull(shop)) {
                log.info("店铺[{}]不存在",id);
                stringRedisTemplate.opsForValue().set(shopKey,"",RedisKeys.CACHE_SHOP_TTL,TimeUnit.MINUTES);
                return ResultDTO.getErrorResult(ErrorResultConstants.SHOP_NOT_EXIST);
            }

            stringRedisTemplate.opsForValue().set(shopKey,JSONUtil.toJsonStr(shop),RedisKeys.CACHE_SHOP_TTL,TimeUnit.MINUTES);
        } catch (Exception e) {
            log.info("店铺[{}]redis缓存重建失败",id);
        } finally {
            unlock(lockKey);
        }

        return ResultDTO.getSuccessResult(shop);
    }

    @Override
    public ResultDTO<Shop> getShopWithLogicTime(Long id) {
        if (Objects.isNull(id)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("正在查询商铺[{}]的数据",id);

        String shopKey = RedisKeys.CACHE_SHOP_KEY + id;
        String jsonRedisData = stringRedisTemplate.opsForValue().get(shopKey);
        Shop shop = null;
        if (StrUtil.isNotBlank(jsonRedisData)) {
            RedisData data = BeanUtil.toBean(jsonRedisData,RedisData.class);
            LocalDateTime expireTime = data.getExpireTime();
            shop = (Shop) data.getData();
            if (LocalDateTime.now().isAfter(expireTime)) {
                log.info("商铺[{}]缓存命中,商铺信息:{}",id,JSONUtil.toJsonStr(shop));
                return ResultDTO.getSuccessResult(shop);
            }
        }

        // 缓存为空或者在逻辑上已经过期了，需要缓存重建
        String lockKey = RedisKeys.MUTEX_LOCK_KEY + id;
        try {
            // 判断是否获得锁，没有锁直接返回缓存中的旧数据，有锁那么直接开启一个异步线程进行缓存重建

        } catch (Exception e) {
            log.info("店铺[{}]redis缓存重建失败",id);
        } finally {
            unlock(lockKey);
        }

        return null;
    }

    private void setLogicTime(String key,Shop shop,Long time,TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        LocalDateTime now = LocalDateTime.now();
        // 逻辑过期时间，在现在的时间上加上xx时间即可
        redisData.setExpireTime(now.plusSeconds(unit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(redisData));
    }

    private boolean tryLock(String key, Long time, TimeUnit unit) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key,"1",time,unit);
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

}
