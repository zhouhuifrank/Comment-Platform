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
 * @description 博客信息实体类
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_blog")
public class Blog extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 21975476L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField(value = "shop_id")
    private Long shopId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "title")
    private String title;

    @TableField(value = "images")
    private String images;

    @TableField(value = "content")
    private String content;

    @TableField(value = "liked")
    private Integer liked;

    @TableField(value = "comments")
    private Integer comments;

}
