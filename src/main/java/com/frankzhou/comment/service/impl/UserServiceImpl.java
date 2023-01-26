package com.frankzhou.comment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.frankzhou.comment.util.PasswordEncoder;
import com.frankzhou.comment.util.RegexUtils;
import com.frankzhou.comment.util.UserLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.Struct;
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
    public ResultDTO<String> sendCode(String phone) {
        // 校验手机号
        if (StringUtils.isEmpty(phone) || RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_INVALID);
        }
        log.info("UserService.sendCode=>phone:{}",phone);

        // 生成验证码 HuTool工具类
        String verifyCode = RandomUtil.randomString(6);

        // 在redis中存验证码并设置过期时间2分钟 使用phone来作为Key 方便从redis中取出value
        stringRedisTemplate.opsForValue().set(RedisKeys.LOGIN_CODE_KEY+phone,verifyCode,RedisKeys.LOGIN_CODE_TTL, TimeUnit.MINUTES);

        log.info("手机验证码发送成功, 验证码: {}",verifyCode);

        return ResultDTO.getSuccessResult(SystemConstants.REQUEST_SUCCESS);
    }

    @Override
    public ResultDTO<String> login(LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("UserService.login=>user:{}", JSONUtil.toJsonStr(loginDTO));

        // 校验手机号
        String phone = loginDTO.getPhone();
        if (RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_INVALID);
        }

        // 校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisKeys.LOGIN_CODE_KEY+phone);
        if (!cacheCode.equals(loginDTO.getCode()) || Objects.isNull(cacheCode)) {
            // 验证码不正确
            log.info("UserService.login verify code is error");
            return ResultDTO.getErrorResult(ErrorResultConstants.CODE_ERROR);
        }

        // 根据手机号查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getPhone,phone).eq(User::getStatus,"NORMAL");
        User user = userMapper.selectOne(wrapper);
        if (Objects.isNull(user)) {
            // 根据手机号创建一个用户
            user = createUserWithPhone(phone);
        }
        log.info("UserService.login user login success=>user:{}",JSONUtil.toJsonStr(user));

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

        log.info("用户{}登录成功",user.getId());
        // 将token返回给前端
        return ResultDTO.getSuccessResult(token);
    }

    @Override
    public ResultDTO<UserDTO> getMe() {
        UserDTO targetDto = UserLocal.getUser();
        return ResultDTO.getSuccessResult(targetDto);
    }

    @Override
    public ResultDTO<User> getUserById(Long userId) {
        if (Objects.isNull(userId)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("UserService.getUserById query user=>userId:{}",userId);

        User user = userMapper.selectById(userId);
        if (Objects.isNull(user)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.DB_QUERY_NO_DATA);
        }

        return ResultDTO.getSuccessResult(user);
    }

    // ----------------------------后端管理接口----------------------------

    @Override
    public ResultDTO<String> loginByPassword(LoginDTO loginDTO) {
        if (Objects.isNull(loginDTO)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("UserService,loginByPassword user is login by password=>user:{}",JSONUtil.toJsonStr(loginDTO));

        String phone = loginDTO.getPhone();
        String password = loginDTO.getPhone();
        // 校验手机号
        if (StrUtil.isBlank(password) || RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_INVALID);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone).eq(User::getStatus,"NORMAL");
        User dbUser = userMapper.selectOne(wrapper);
        if (Objects.isNull(dbUser)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.USER_NOT_EXIST);
        }

        // 校验密码
        if (RegexUtils.passwordIsInvalid(password)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PASSWORD_ERROR);
        }

        Boolean passwordMatch = PasswordEncoder.isMatch(password,dbUser.getPassword());
        if (!passwordMatch) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PASSWORD_ERROR);
        }
        // 同样也存入Redis中，生成token
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(dbUser,userDTO);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((key, value) -> value.toString()));
        // 生成token，存入redis中
        String token = UUID.fastUUID().toString(true);
        String tokenKey = RedisKeys.LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        stringRedisTemplate.expire(tokenKey,RedisKeys.LOGIN_USER_TTL,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(SystemConstants.REQUEST_SUCCESS);
    }

    @Override
    public ResultDTO<String> register(RegisterDTO registerDTO) {
        if (Objects.isNull(registerDTO)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("UserService,register=>register info:{}",JSONUtil.toJsonStr(registerDTO));

        String phone = registerDTO.getPhone();
        String password = registerDTO.getPhone();
        String nickName = registerDTO.getNickName();
        // 校验手机号是否合法
        if (StrUtil.isBlank(phone) || RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_INVALID);
        }
        // 校验手机号是否重复 即用户是否重复注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone).eq(User::getStatus,"NORMAL");
        User dbUser = userMapper.selectOne(wrapper);
        if (!Objects.isNull(dbUser)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PHONE_IS_DUPLICATED);
        }

        if (StrUtil.isBlank(password) || RegexUtils.passwordIsInvalid(password)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PASSWORD_ERROR);
        }

        if (StrUtil.isBlank(nickName)) {
            // 昵称为空，先随机生成默认昵称
            String randomName = SystemConstants.USER_NAME_PREFIX + RandomUtil.randomNumbers(10);
            registerDTO.setNickName(randomName);
        }
        // 密码加密
        String encodePassword = PasswordEncoder.encode(password);
        User user = new User();
        BeanUtil.copyProperties(registerDTO,user);
        user.setPassword(encodePassword);
        user.setIcon("");
        try {
            Integer insertCount = userMapper.insert(user);
            if (insertCount != 1) {
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
            }
        } catch (Exception e) {
            log.info("DataBase error");
        }

        return ResultDTO.getSuccessResult(SystemConstants.REQUEST_SUCCESS);
    }

    // TODO 重设密码可能有问题
    @Override
    public ResultDTO<String> updatePassword(RegisterDTO registerDTO) {
        if (Objects.isNull(registerDTO)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }
        log.info("UserService.forgetPassword=>register:{}",JSONUtil.toJsonStr(registerDTO));

        String phone = registerDTO.getPhone();
        String password = registerDTO.getPassword();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone).eq(User::getStatus,"NORMAL");
        User user = userMapper.selectOne(wrapper);

        if (Objects.isNull(user)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.USER_NOT_EXIST);
        }
        // 密码与数据库中不一致
        if (PasswordEncoder.isMatch(password,user.getPassword())) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PASSWORD_ERROR);
        }

        if (password.equals(registerDTO.getNewPassword())) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PASSWORD_NOT_EQUAL);
        }

        String encodePassword = PasswordEncoder.encode(password);
        user.setPassword(encodePassword);
        try {
            Integer updateCount = userMapper.update(user, wrapper);
            if (updateCount != 1) {
                return ResultDTO.getErrorResult(ErrorResultConstants.DB_ERROR);
            }
        } catch (Exception e) {
            log.info("DataBase error");
        }

        return ResultDTO.getSuccessResult(SystemConstants.REQUEST_SUCCESS);
    }

    @Override
    public ResultDTO<String> forgetPassword(RegisterDTO registerDTO) {
        return null;
    }

    // TODO mybatis-plus的updateById方法会把icon覆盖掉
    @Override
    public ResultDTO<String> updateUser(User user) {
        if (Objects.isNull(user) || Objects.isNull(user.getId())) {
            return ResultDTO.getErrorResult(ErrorResultConstants.PARAMS_ERROR);
        }

        User dbUser = userMapper.selectById(user.getId());
        if (Objects.isNull(dbUser)) {
            return ResultDTO.getErrorResult(ErrorResultConstants.USER_NOT_EXIST);
        }

        userMapper.updateById(user);

        return ResultDTO.getSuccessResult(SystemConstants.REQUEST_SUCCESS);
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
