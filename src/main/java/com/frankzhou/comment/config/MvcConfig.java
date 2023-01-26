package com.frankzhou.comment.config;

import com.frankzhou.comment.common.interceptor.LoginInterceptor;
import com.frankzhou.comment.common.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebMvc配置类 配置拦截器
 * @date 2023-01-14
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置Token刷新拦截器 order用来设置优先级 刷新token拦截器拦截所有的路径
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).order(0);
        // 配置登录拦截器
        /*
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(  // 排除路径，不拦截
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                ).order(1);
        */
        // 为了测试方便，所有路径都不拦截
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/**"
                ).order(1);
    }
}
