package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.Shop;
import com.frankzhou.comment.service.IShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺信息控制器
 * @date 2023-01-15
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private IShopService shopService;

    @GetMapping("/{id}")
    public ResultDTO<Shop> getShopById(@PathVariable("id") Long id) {
        return null;
    }


}
