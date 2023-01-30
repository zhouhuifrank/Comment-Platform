package com.frankzhou.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.entity.SeckillVoucher;
import com.frankzhou.comment.entity.Voucher;
import com.frankzhou.comment.entity.VoucherOrder;
import com.frankzhou.comment.mapper.SeckillVoucherMapper;
import com.frankzhou.comment.mapper.VoucherMapper;
import com.frankzhou.comment.mapper.VoucherOrderMapper;
import com.frankzhou.comment.redis.RedisIdGenerator;
import com.frankzhou.comment.redis.SimpleRedisLock;
import com.frankzhou.comment.service.IVoucherOrderService;
import com.frankzhou.comment.util.UserLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷订单服务实现类
 * @date 2023-01-19
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl implements IVoucherOrderService {

    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    @Resource
    private RedisIdGenerator redisIdGenerator;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultDTO<Long> getSeckillOrder(Long voucherId) {
        if (Objects.isNull(voucherId)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        QueryWrapper<SeckillVoucher> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SeckillVoucher::getVoucherId,voucherId);
        SeckillVoucher seckillVoucher = seckillVoucherMapper.selectOne(wrapper);
        if (Objects.isNull(seckillVoucher)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_QUERY_NO_DATA);
        }

        // 校验是否在优惠卷时间内
        LocalDateTime now = LocalDateTime.now();
        if (seckillVoucher.getBeginTime().isAfter(now) && seckillVoucher.getEndTime().isBefore(now)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.NOT_IN_VOUCHER_TIME);
        }

        // 校验库存是否充足
        if (seckillVoucher.getStock() < 1) {
            return ResultDTO.getErrorResult(ErrorResultConstants.STOCK_NOT_ENOUGH);
        }

        Long userId = UserLocal.getUser().getId();
        SimpleRedisLock redisLock = new SimpleRedisLock("order"+userId,stringRedisTemplate);
        boolean isLock = redisLock.tryLock(10L);
        // 获取锁失败
        if (!isLock) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DUPLICATED_VOUCHER_ORDER);
        }

        try {
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId,seckillVoucher.getStock());
        } finally {
            redisLock.unlock();;
        }

        /*
        Long userId = UserLocal.getUser().getId();
        synchronized (userId.toString().intern()) {
            // 拿到代理对象 保证事务正常执行，因此spring的事务是通过代理对象的，而这个方法默认是this指向
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.getSeckillOrder(voucherId);
        }
        */
    }


    @Override
    @Transactional
    public ResultDTO<Long> createVoucherOrder(Long voucherId,Integer stock) {
        // 查询用户订单，保证一人一单 使用分布式锁保证集群情况下的线程安全
        Long userId = UserLocal.getUser().getId();
        Integer count = voucherOrderMapper.selectOrderCount(userId, voucherId);
        if (count > 0) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DUPLICATED_VOUCHER_ORDER);
        }

        // 使用乐观锁防止库存超卖
        Integer deductCount = seckillVoucherMapper.deductStockCount(voucherId, stock);
        if (deductCount == 0) {
            return ResultDTO.getErrorResult(ErrorResultConstants.STOCK_NOT_ENOUGH);
        }

        // 保存用户订单
        VoucherOrder voucherOrder = new VoucherOrder();
        String key = "voucher:order:";
        Long globalId = redisIdGenerator.nextId(key);
        voucherOrder.setId(globalId);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        try {
            Integer insertCount = voucherOrderMapper.insert(voucherOrder);
            if (insertCount < 1) {
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
            }
        } catch (Exception e) {
            log.info("Database error");
        }

        return ResultDTO.getSuccessResult(voucherOrder.getId());
    }
}
