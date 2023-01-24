package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.entity.Voucher;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷前端控制器
 * @date 2023-01-19
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @PostMapping("/addSeckill")
    public ResultDTO<Long> addSecKillVoucher(@RequestBody Voucher voucher) {
        return null;
    }

    @PostMapping("/addVoucher")
    public ResultDTO<Long> addVoucher(@RequestBody Voucher voucher) {
        return null;
    }

    @GetMapping("/list/{id}")
    public ResultDTO<List<Voucher>> getVoucherList(@PathVariable("id") String shopId) {
        return null;
    }
}
