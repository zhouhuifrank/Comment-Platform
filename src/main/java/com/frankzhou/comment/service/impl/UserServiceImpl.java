package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.comment.common.ResultDTO;
import com.frankzhou.comment.common.constants.ErrorResultConstants;
import com.frankzhou.comment.common.constants.SystemConstants;
import com.frankzhou.comment.dto.LoginDTO;
import com.frankzhou.comment.dto.RegisterDTO;
import com.frankzhou.comment.dto.UserDTO;
import com.frankzhou.comment.entity.User;
import com.frankzhou.comment.mapper.UserMapper;
import com.frankzhou.comment.redis.RedisKeys;
import com.frankzhou.comment.service.IUserService;
import com.frankzhou.comment.util.RegexUtils;
import com.frankzhou.comment.util.UserLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理接口实现类
 * @date 2023-01-14
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultDTO<Boolean> sendCode(String phone, HttpSession session) {
        // 校验手机号
        if (StringUtils.isEmpty(phone) || RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_INVALID);
        }

        // 生成验证码 hutool工具类
        String verifyCode = RandomUtil.randomNumbers(6);

        // 在redis中存验证码并设置过期时间 使用phone来作为Key 方便从redis中取出value
        stringRedisTemplate.opsForValue().set(RedisKeys.LOGIN_CODE_KEY+phone,verifyCode,RedisKeys.LOGIN_CODE_TTL, TimeUnit.MINUTES);

        log.info("手机验证码发送成功, 验证码: {}",verifyCode);

        return ResultDTO.getSuccessResult();
    }

    @Override
    public ResultDTO<String> login(LoginDTO loginDTO, HttpSession session) {
        if (Objects.isNull(loginDTO)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        // 校验手机号
        String phone = loginDTO.getPhone();
        if (RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_INVALID);
        }

        // 校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisKeys.LOGIN_CODE_KEY+phone);
        if (!cacheCode.equals(loginDTO.getCode()) || Objects.isNull(cacheCode)) {
            // 验证码不正确
            return ResultDTO.getErrorResult(ErrorResultConstants.CODE_ERROR);
        }

        // 根据手机号查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getPhone,phone);
        User user = userMapper.selectOne(wrapper);
        if (Objects.isNull(user)) {
            // 根据手机号创建一个用户
            user = createUserWithPhone(phone);
        }

        // 保存到Redis中 使用Hash来保存(注意StringRedisTemplate中要求value中的对象所有字段都必须得为String类型)
        // 生成随机的Token作为key
        String token = UUID.fastUUID().toString(true);
        // 将User对象转换为Map
        UserDTO targetDTO = new UserDTO();
        BeanUtil.copyProperties(user,targetDTO);
        Map<String,Object> userMap = BeanUtil.beanToMap(targetDTO, new HashMap<>(),
                CopyOptions.create().
                        setIgnoreNullValue(true).
                        setFieldValueEditor((key, value) -> value.toString()));
        // 保存到redis中
        String tokenKey = RedisKeys.LOGIN_USER_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        stringRedisTemplate.expire(RedisKeys.LOGIN_USER_KEY+token,RedisKeys.LOGIN_USER_TTL,TimeUnit.MINUTES);

        // 将token返回给前端
        return ResultDTO.getSuccessResult(token);
    }

    @Override
    public ResultDTO<String> loginByPassword(LoginDTO loginDTO, HttpSession session) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> register(RegisterDTO registerDTO) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> forgetPassword(RegisterDTO registerDTO) {
        return null;
    }

    @Override
    public ResultDTO<UserDTO> getMe() {
        UserDTO targetDto = UserLocal.getUser();
        return ResultDTO.getSuccessResult(targetDto);
    }

    @Override
    public ResultDTO<User> getUserById(Long userId) {
        return null;
    }


    private User createUserWithPhone(String phone) {
        User user = new User();
        String name = SystemConstants.USER_NAME_PREFIX + RandomUtil.randomNumbers(10);
        user.setPhone(phone);
        user.setNickName(name);
        userMapper.insert(user);

        return user;
    }
}
