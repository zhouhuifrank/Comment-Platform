package com.frankzhou.comment.controller;

import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.dto.VoucherOrderQueryDTO;
import com.frankzhou.comment.entity.VoucherOrder;
import com.frankzhou.comment.service.IVoucherOrderService;
import com.frankzhou.comment.service.IVoucherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷订单控制器
 * @date 2023-01-19
 */
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @PostMapping("/seckill/{id}")
    public ResultDTO<Long> getSeckillOrder(@PathVariable("id") Long voucherId) {
        return voucherOrderService.getSeckillOrder(voucherId);
    }

    // ---------------------后台管理接口--------------------

    @GetMapping("/{id}")
    public ResultDTO<List<VoucherOrder>> getVoucherOrderById(@PathVariable("id") Long VoucherId) {
        return null;
    }

    @PostMapping("/page")
    public ResultDTO<List<VoucherOrder>> getVoucherOrderByPage(@RequestBody VoucherOrderQueryDTO queryDTO) {
        return null;
    }
}
