package com.frankzhou.comment.redis;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 封装redis对象 逻辑过期时间
 * @date 2023-01-15
 */
@Data
@Builder
public class RedisData {
    private LocalDateTime expireTime;
    Object data;
}
