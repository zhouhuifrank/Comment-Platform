package com.frankzhou.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 秒杀优惠卷实体类
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_seckill_voucher")
public class SeckillVoucher extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 21889566L;

    @TableId(value = "voucher_id",type = IdType.INPUT)
    private Long voucherId;

    @TableField(value = "stock")
    private Integer stock;

    @TableField(value = "begin_time")
    private LocalDateTime beginTime;

    @TableField(value = "end_time")
    private LocalDateTime endTime;

}
