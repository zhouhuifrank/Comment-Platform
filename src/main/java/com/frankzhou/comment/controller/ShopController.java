package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.Shop;
import com.frankzhou.comment.service.IShopService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 店铺信息控制器  查询店铺缓存的各种方案
 * @date 2023-01-15
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    private IShopService shopService;

    @GetMapping("/{id}")
    public ResultDTO<Shop> getShopById(@PathVariable("id") Long id) {
        return shopService.getShopByIdString(id);
    }

    @GetMapping("/hash/{id}")
    public ResultDTO<Shop> getShopByIdHash(@PathVariable("id") Long id) {
        return shopService.getShopByIdHash(id);
    }

    @GetMapping("/penetrate/{id}")
    public ResultDTO<Shop> getShopByIdPenetrate(@PathVariable("id") Long id) {
        return shopService.getShopWithPenetrate(id);
    }

    @GetMapping("/mutex/{id}")
    public ResultDTO<Shop> getShopByIdMutex(@PathVariable("id") Long id) {
        return shopService.getShopWithMutex(id);
    }

    @GetMapping("/logic/{id}")
    public ResultDTO<Shop> getShopByIdLogic(@PathVariable("id") Long id) {
        return shopService.getShopWithLogicTime(id);
    }

    @PostMapping("/add")
    public ResultDTO<Long> saveShop(@RequestBody Shop shop) {
        return shopService.saveShop(shop);
    }

    @PostMapping("/update")
    public ResultDTO<String> updateShop(@RequestBody Shop shop) {
        return shopService.updateShop(shop);
    }

    @GetMapping("/of/type")
    public ResultDTO<Shop> queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "x", required = false) Double y) {
        return null;
    }


    @GetMapping("/of/name")
    public ResultDTO<List<Shop>> queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        return null;
    }
}
