package com.frankzhou.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 优惠卷实体类
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_voucher")
public class Voucher extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 21313566L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField(value = "shop_id")
    private Long shopId;

    @TableField(value = "title")
    private String title;

    @TableField(value = "sub_title")
    private String subTitle;

    @TableField(value = "rules")
    private String rules;

    @TableField(value = "pay_value")
    private Long payValue;

    @TableField(value = "actual_value")
    private Long actualValue;

    @TableField(value = "type")
    private Integer type;

    @TableField(value = "status")
    private Integer status;

}
