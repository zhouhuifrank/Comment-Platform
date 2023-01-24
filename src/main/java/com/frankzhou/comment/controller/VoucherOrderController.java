package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷订单控制器
 * @date 2023-01-19
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @PostMapping("/seckill/{id}")
    public ResultDTO<Long> getSeckillOrder(@PathVariable("id") Long voucherId) {
        return null;
    }
}
