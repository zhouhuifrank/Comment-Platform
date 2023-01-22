package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.Shop;

import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺信息服务接口
 * @date 2023-01-15
 */
public interface IShopService {

    /**
     * 更新商铺 先更新数据库，再删除缓存
     *
     * @author this.FrankZhou
     * @param shop 店铺信息
     * @return Long 店铺id
     */
    ResultDTO<Long> updateShop(Shop shop);

    /**
     * 通过id查询商铺并存入Redis缓存 Hash
     * 对于商铺数据，使用手动更新+设置过期时间的方式来更新缓存
     *
     * @author this.FrankZhou
     * @param id 商铺id
     * @return Shop
     */
    ResultDTO<Shop> getShopByIdHash(Long id);

    /**
     * 通过id查询商铺并存入Redis缓存 String
     * 对于商铺数据，使用手动更新+设置过期时间的方式来更新缓存
     *
     * @author this.FrankZhou
     * @param id 商铺id
     * @return Shop
     */
    ResultDTO<Shop> getShopByIdString(Long id);

    /**
     * 通过id查询商铺并存入Redis缓存 String
     * 手动更新+设置TTL过期时间解决数据一致性 解决缓存穿透 设置随机TTL时间解决大量key同时失效问题
     *
     * @author this.FrankZhou
     * @param id 商铺id
     * @return Shop
     */
    ResultDTO<Shop> getShopWithPenetrate(Long id);

    /**
     * 通过id查询商铺并存入Redis缓存 String
     * 手动更新+设置TTL过期时间解决数据一致性 使用互斥锁解决缓存击穿 设置随机TTL时间解决大量key同时失效问题
     *
     * @author this.FrankZhou
     * @param id 商铺id
     * @return Shop
     */
    ResultDTO<Shop> getShopWithMutex(Long id);

    /**
     * 通过id查询商铺并存入Redis缓存 String
     * 手动更新+设置TTL过期时间解决数据一致性 设置逻辑过期时间解决缓存击穿 设置随机TTL时间解决大量key同时失效问题
     *
     * @author this.FrankZhou
     * @param id 店铺id
     * @return Shop
     */
    ResultDTO<Shop> getShopWithLogicTime(Long id);

}
