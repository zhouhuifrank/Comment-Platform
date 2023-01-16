package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.ShopType;
import com.frankzhou.comment.service.IShopTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return null;
    }
}
