package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.VoucherOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷订单mapper->sql
 * @date 2023-01-19
 */
@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {

    Integer selectOrderCount(@Param("userId") Long userId, @Param("voucherId") Long voucherId);
}
