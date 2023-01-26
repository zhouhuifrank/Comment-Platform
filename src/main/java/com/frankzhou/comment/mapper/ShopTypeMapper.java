package com.frankzhou.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.comment.entity.ShopType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 商铺类型mapper sql
 * @date 2023-01-15
 */
@Mapper
public interface ShopTypeMapper extends BaseMapper<ShopType> {

    /**
     * 查询所有的店铺类型信息
     *
     * @author this.FrankZhou
     * @return List<ShopType>
     */
    List<ShopType> queryAllShopTypeList();

    /**
     * 批量插入店铺类型信息
     *
     * @author this.FrankZhou
     * @return 插入数据数量
     */
    Integer batchInsert();

    /**
     * 批量更新店铺类型信息
     *
     * @author this.FrankZhou
     * @return 更新数据信息
     */
    Integer batchUpdate();
}
