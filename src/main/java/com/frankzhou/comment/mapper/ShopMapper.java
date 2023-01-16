package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.Shop;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 商铺信息mapper sql
 * @date 2023-01-15
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
}
