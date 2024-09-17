package com.yzx.reggie.common;

/**
 * 基于threatLocal封装工具类，用于保存和获取当前登录的用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){
        System.out.println("sssdsss");
        System.out.println("toooooasd");
        System.out.println("ttattoua");
        threadLocal.set(id);

        System.out.println("测试");
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
