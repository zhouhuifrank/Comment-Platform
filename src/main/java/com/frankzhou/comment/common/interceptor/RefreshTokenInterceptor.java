package com.frankzhou.comment.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.druid.util.StringUtils;
import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.util.UserLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Token刷新拦截器
 * @date 2023-01-14
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {
    // 这里由于拦截器没有被Spring管理，因此不同注入StringRedisTemplate，需要通过构造函数传入
    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token)) {
            // 放行不刷新token
            log.info("token不存在");
            return true;
        }

        // 从redis中取出user
        String tokenKey = RedisKeys.LOGIN_USER_KEY + token;
        Map<Object,Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        if (MapUtil.isEmpty(userMap)) {
            log.info("用户登录信息不存在");
            return true;
        }

        // 转换为User对象存入ThreadLocal
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        UserLocal.setUser(userDTO);
        // 刷新StringRedisTemplate中的token
        stringRedisTemplate.expire(tokenKey, RedisKeys.LOGIN_USER_TTL, TimeUnit.MINUTES);
        log.info("刷新token成功:{}",token);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 移除用户
        UserLocal.removeUser();
    }
}
