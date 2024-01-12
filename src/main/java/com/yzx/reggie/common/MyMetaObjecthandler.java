package com.yzx.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {
    //update或inserth时才会调用
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        //这里如何获取当前操作的真正的empId呢，可以使用threadlocal, 它相当于是以每个线程作为作用域，为每个线程单独提供一份存储空间，不同线程之间是隔离的，不用担心变量混淆
        //目前一个线程的流程先LoginCheckfilter--->UpdateController--->MyMetaobjecthandler是一个线程操作的
        /**
         * 实现步骤
         * 1.编写BaseContext工具类，基于ThreadLocal封装的工具类
         * 2.在LoginCheckFilter的doFilter方法中调用BaseContext来设置当前登录用户的id
         * 3.在MyMetaObjecthandler的方法中调用BaseContext获取登录用户的id
         */
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]");
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
        long id = Thread.currentThread().getId();
        log.info("线程id为{}",id);
    }
}
