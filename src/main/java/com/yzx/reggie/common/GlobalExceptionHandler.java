package com.yzx.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，底层原理就是代理这些controller，通过aop可以controller方法拦截到，抛异常了可以统一在这里处理
 */
//告诉异常拦截器，带了这些注解的controller出以下特定异常会被拦截
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//返回json格式数据
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 全局异常处理器
     * 异常处理方法,当检测到controller出现这种异常时会在这里处理
     * @return
     * //主要时这个注解在起作用，方法名随意
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        //Duplicate entry '1234' for key 'employee.idx_username'
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    /**
     * 处理删除category关联到菜品or套餐时的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomerException.class)
    public R<String> exceptionHandler(CustomerException ex){
        log.error(ex.getMessage());


        return R.error(ex.getMessage());
    }
}
