package com.frankzhou.comment.util;

import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.entity.User;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 存放UserDTO的threadLocal
 * @date 2023-01-14
 */
public class UserLocal {

    private static final ThreadLocal<UserDTO> local = new ThreadLocal<>();

    public static void setUser(UserDTO userDTO) {
        local.set(userDTO);
    }

    public static UserDTO getUser() {
        return local.get();
    }

    public static void removeUser() {
        local.remove();
    }

}
