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
 * @description 博客评论信息
 * @date 2023-01-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName(value = "tb_blog_comments")
public class BlogComments extends BaseEntity implements Serializable {
    private static final Long serialVersionUID = 21829166L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "blog_id")
    private Long blogId;

    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(value = "answer_id")
    private Long answerId;

    @TableField(value = "content")
    private String content;

    @TableField(value = "liked")
    private Integer liked;

    @TableField(value = "status")
    private Integer status;

}
