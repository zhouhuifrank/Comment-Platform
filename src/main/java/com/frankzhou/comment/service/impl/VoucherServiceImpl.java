package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.entity.SeckillVoucher;
import com.frankzhou.comment.entity.Voucher;
import com.frankzhou.comment.mapper.SeckillVoucherMapper;
import com.frankzhou.comment.mapper.VoucherMapper;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.IVoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷服务实现类
 * @date 2023-01-19
 */
@Slf4j
@Service
public class VoucherServiceImpl implements IVoucherService {

    @Resource
    private VoucherMapper voucherMapper;

    @Resource
    private SeckillVoucherMapper seckillVoucherMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultDTO<Long> addVoucher(Voucher voucher) {
        if (Objects.isNull(voucher)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        Integer insertCount = voucherMapper.insert(voucher);
        if (insertCount < 1) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        return ResultDTO.getSuccessResult(voucher.getId());
    }

    @Override
    public ResultDTO<Long> addSeckillVoucher(Voucher voucher) {
        if (Objects.isNull(voucher)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        Integer insertCount = seckillVoucherMapper.insert(seckillVoucher);
        if (insertCount < 1) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        // redis中存入库存信息用于秒杀 使用优惠卷id查询缓存
        String voucherKey = RedisKeys.VOUCHER_STOCK_KEY + voucher.getId();
        stringRedisTemplate.opsForValue().set(voucherKey,voucher.getStock().toString());

        return ResultDTO.getSuccessResult(voucher.getId());
    }

    @Override
    public ResultDTO<List<Voucher>> getVoucherList(Long shopId) {
        if (Objects.isNull(shopId)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        String voucherListKey = RedisKeys.VOUCHER_LIST_KEY + shopId;
        List<String> cacheVoucherList = stringRedisTemplate.opsForList().range(voucherListKey, 0, -1);
        List<Voucher> voucherList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(cacheVoucherList)) {
            for (String cacheVoucher : cacheVoucherList) {
                Voucher voucher = BeanUtil.toBean(cacheVoucher, Voucher.class);
                voucherList.add(voucher);
            }

            return ResultDTO.getSuccessResult(voucherList);
        }

        // 缓存不存在
        try {
            LambdaQueryWrapper<Voucher> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Voucher::getShopId,shopId);
            voucherList = voucherMapper.selectList(wrapper);
            if (CollectionUtil.isEmpty(voucherList)) {
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_QUERY_NO_DATA);
            }
        } catch (Exception e) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
        }

        List<String> jsonVoucherList = voucherList.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());
        stringRedisTemplate.opsForList().leftPushAll(voucherListKey,jsonVoucherList);

        return ResultDTO.getSuccessResult(voucherList);
    }


}
