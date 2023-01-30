package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 秒杀优惠卷mapper->sql
 * @date 2023-01-19
 */
@Mapper
public interface SeckillVoucherMapper extends BaseMapper<SeckillVoucher> {

    Integer deductStockCount(@Param("voucherId") Long voucherId, @Param("stock") Integer stock);
}
