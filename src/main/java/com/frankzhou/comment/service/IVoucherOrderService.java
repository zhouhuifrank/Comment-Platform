package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷订单服务接口
 * @date 2023-01-19
 */
public interface IVoucherOrderService {

    /**
     * 抢购秒杀优惠卷
     *
     * @author this.FrankZhou
     * @param voucherId 优惠卷Id
     * @return Long 生成的订单Id
     */
    ResultDTO<Long> getSeckillOrder(Long voucherId);

    /**
     * 创建用户的优惠卷订单 保证优惠卷不超卖且一人只能下一单
     *
     * @author this.Frankzhou
     * @param voucherId 优惠卷Id
     * @param stock 优惠卷库存
     * @return Long 生成的订单Id
     */
    ResultDTO<Long> createVoucherOrder(Long voucherId,Integer stock);
}
