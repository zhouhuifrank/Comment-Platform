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
 * @description 用户登录信息实体类
 * @date 2023-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_user")
public class User extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 122529114L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "password")
    private String password;

    @TableField(value = "nick_name")
    private String nickName;

    @TableField(value = "icon")
    private String icon = "";

    @TableField(value = "status",fill = FieldFill.INSERT)
    private String status;
}
