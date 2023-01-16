package com.frankzhou.comment.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 基类 公共属性
 * @date 2023-01-15
 */
@Data
public abstract class BaseEntity implements Serializable {
    private static final Long serialVersionUID = 21738718L;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
