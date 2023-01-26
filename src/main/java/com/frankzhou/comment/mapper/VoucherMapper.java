package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷mapper->sql
 * @date 2023-01-19
 */
@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {
}
