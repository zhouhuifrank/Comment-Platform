package com.frankzhou.comment.common.interceptor;

import cn.hutool.http.HttpStatus;
import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.util.UserLocal;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 登录拦截器
 * @date 2023-01-14
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDTO userDTO = UserLocal.getUser();

        if (Objects.isNull(userDTO)) {
            // 用户不存在，拦截
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            return false;
        }

        // 放行
        return true;
    }
}
