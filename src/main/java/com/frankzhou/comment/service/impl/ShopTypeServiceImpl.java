package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.ShopType;
import com.frankzhou.comment.mapper.ShopTypeMapper;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.IShopTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺类型服务实现类
 * @date 2023-01-15
 */
@Slf4j
@Service
public class ShopTypeServiceImpl implements IShopTypeService {

    @Resource
    private ShopTypeMapper shopTypeMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ResultDTO<List<ShopType>> getShopTypeList() throws JsonProcessingException {
        String listKey = RedisKeys.CACHE_SHOP_TYPE_KEY;
        // range函数从0~-1表示查询对应key中list的所有数据
        List<String> redisTypeList = stringRedisTemplate.opsForList().range(listKey, 0, -1);
        List<ShopType> typeList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(redisTypeList) || redisTypeList.size() != 0) {
            // 商铺类型列表不空，转换对象返回结果 TODO BeanUtil使用出错 转换为空值
            for (String typeJson : redisTypeList) {
                ShopType shopType  = mapper.readValue(typeJson, ShopType.class);
                typeList.add(shopType);
            }
            log.info("Redis缓存查询成功，店铺类型列表");
            return ResultDTO.getSuccessResult(typeList);
        }

        // 缓存不存在
        typeList = shopTypeMapper.queryAllShopTypeList();
        // 将List中的每一个元素都序列化
        List<String> cacheTypeList = typeList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().leftPushAll(listKey,cacheTypeList);

        return ResultDTO.getSuccessResult(typeList);
    }
}
