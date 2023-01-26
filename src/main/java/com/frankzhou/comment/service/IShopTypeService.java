package com.frankzhou.comment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.ShopType;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺类型服务接口
 * @date 2023-01-15
 */
public interface IShopTypeService {

    /**
     * 获取所有的商铺类型信息并存入Redis缓存 List
     * 对于商铺类型这种一致性要求低的数据，使用redis的LRU缓存淘汰机制来更新缓存
     * 使用jackson的objectMapper进行类型转换
     * @author this.FrankZhou
     * @return List<ShopType> 商铺类型集合
     */
    ResultDTO<List<ShopType>> getShopTypeList();

    /**
     * 获取所有商铺类型信息并存入Redis缓存 List
     * 使用HuTool工具类进行类型转换
     * @author this.FrankZhou
     * @return List<ShopType> 商铺类型集合
     */
    ResultDTO<List<ShopType>> queryShopTypeList();

    /**
     * 批量修改商铺类型数据
     *
     * @author this.FrankZhou
     * @param shopTypeList 商铺类型列表
     * @return string 请求结果
     */
    ResultDTO<String> batchUpdateTypeList(List<ShopType> shopTypeList);

    /**
     * 批量新增商铺类型数据
     *
     * @author this.FrankZhou
     * @param shopTypeList 商铺类型列表
     * @return string 请求结果
     */
    ResultDTO<String> batchInsertTypeList(List<ShopType> shopTypeList);
}
