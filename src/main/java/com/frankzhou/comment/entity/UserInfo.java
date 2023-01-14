package com.frankzhou.comment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户登录信息表
 * @date 2023-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_user_info")
public class UserInfo implements Serializable {
    private static final Long serialVersionUID = 123888214L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField(value = "city")
    private String city;

    @TableField(value = "introduce")
    private String introduce;

    @TableField(value = "fans")
    private Integer fans;

    @TableField(value = "followee")
    private Integer followee;

    @TableField(value = "gender")
    private Integer gender;

    @TableField(value = "birthday")
    private Date birthday;

    @TableField(value = "credits")
    private Integer credits;

    @TableField(value = "level")
    private Integer level;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_tine", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
