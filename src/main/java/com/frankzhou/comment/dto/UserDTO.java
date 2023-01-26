package com.frankzhou.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户返回传输对象 返回给前端显示并且用户
 * @date 2023-01-14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;

    private String nickName;

    private String icon;

}
