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
 * @description 店铺信息实体类
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_shop")
public class Shop extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 2738181L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "type_id")
    private Long typeId;

    @TableField(value = "images")
    private String images;

    @TableField(value = "area")
    private String area;

    @TableField(value = "address")
    private String address;

    @TableField(value = "x")
    private double x;

    @TableField(value = "y")
    private double y;

    @TableField(value = "avg_price")
    private Long avgPrice;

    @TableField(value = "sold")
    private Integer sold;

    @TableField(value = "comments")
    private Integer comments;

    @TableField(value = "score")
    private Integer score;

    @TableField(value = "open_hours")
    private String openHours;

}
