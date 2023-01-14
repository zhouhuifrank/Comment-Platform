package com.frankzhou.comment.common.support;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Mybatis-Plus公共字段自动填充
 * @date 2023-01-14
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 创建时字段填充策略
     *
     * @author this.FrankZhou
     * @params metaObject 元数据对象
     * @return void
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime",LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
    }

    /**
     * 更新时字段填充策略
     *
     * @author this.FrankZhou
     * @params metaObject 元数据对象
     * @return void
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
    }
}
