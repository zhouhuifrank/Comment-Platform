package com.frankzhou.comment.service;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.Voucher;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷服务接口
 * @date 2023-01-19
 */
public interface IVoucherService {

    /**
     * 添加普通优惠卷
     * 
     * @author this.FrankZhou
     * @param voucher 优惠卷
     * @return voucherId 添加的优惠卷id
     */
    ResultDTO<Long> addVoucher(Voucher voucher);

    /**
     * 添加秒杀优惠卷
     * 
     * @author this.FrankZhou
     * @param voucher 附带秒杀信息的优惠卷
     * @return voucherId 添加的优惠卷id
     */
    ResultDTO<Long> addSeckillVoucher(Voucher voucher);
    
    /**
     * 获取优惠卷的秒杀信息 查询缓存
     * 
     * @author this.FrankZhou
     * @param shopId 店铺id
     * @return List<Voucher> 店铺的优惠卷列表
     */
    ResultDTO<List<Voucher>> getVoucherList(Long shopId);
}
