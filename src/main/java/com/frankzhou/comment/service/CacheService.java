package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.Shop;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Redis缓存服务
 * @date 2023-07-30
 */
public interface CacheService {

    /**
     * 保存店铺信息
     */
    ResultDTO<Long> saveShop(Shop shop);

    /**
     * 更新店铺信息 先更新数据库，在删除缓存
     */
    ResultDTO<Boolean> updateShop(Shop shop);

    /**
     * 查询缓存 旁路缓存模式
     */
    ResultDTO<Shop> queryShopWithCacheAside(Long id);

    /**
     * 查询缓存 解决缓存穿透
     * 缓存空对象
     */
    ResultDTO<Shop> queryShopWithPenetrate(Long id);

    /**
     * 查询缓存 解决缓存击穿
     * 互斥锁缓存重建
     */
    ResultDTO<Shop> queryShopWithMutex(Long id);

    /**
     * 查询缓存 解决缓存击穿
     * 设置逻辑过期时间 互斥锁缓存重建
     */
    ResultDTO<Shop> queryShopWithLogicTime(Long id);
}
