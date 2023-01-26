package com.frankzhou.comment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.ShopType;
import com.frankzhou.comment.service.IShopTypeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 商铺类型控制器
 * @date 2023-01-15
 */
@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {

    @Resource
    private IShopTypeService shopTypeService;

    @GetMapping("/list")
    public ResultDTO<List<ShopType>> getShopTypeList() {
        return shopTypeService.getShopTypeList();
    }

    // TODO 有小bug
    @GetMapping("/queryList")
    public ResultDTO<List<ShopType>> queryShopTypeList() {
        return shopTypeService.queryShopTypeList();
    }

    // -------------------后台管理接口---------------------

    @PostMapping("/update")
    public ResultDTO<String> updateShopTypeList(@RequestBody List<ShopType> shopTypeList) {
        return shopTypeService.batchUpdateTypeList(shopTypeList);
    }

    @PostMapping("/add")
    public ResultDTO<String> addShopTypeList(@RequestBody List<ShopType> shopTypeList) {
        return shopTypeService.batchInsertTypeList(shopTypeList);
    }
}
