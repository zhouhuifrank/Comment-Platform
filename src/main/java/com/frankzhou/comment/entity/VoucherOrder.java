package com.frankzhou.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷订单实体类
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_voucher_order")
public class VoucherOrder extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 21213546L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "voucher_id")
    private Long voucherId;

    @TableField(value = "pay_type")
    private Integer payType;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "pay_time")
    private LocalDateTime payTime;

    @TableField(value = "use_time")
    private LocalDateTime useTime;

    @TableField(value = "refund_time")
    private LocalDateTime refundTime;

}
