package com.yzx.reggie;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * java项目结构解析
 * enetity层（也叫domian层和model层），实体层用于存放我们的实体类，与数据库中的属性值基本保持一致，实现get/set 方法
 * mapper层（也叫dao层），对数据库进行数据持久化操作，它的方法语句是直接对数据库操作的，主要实现一些增删改查操作，在mybatis中方法主要与xxx.xml内相互一一映射
 * service层（业务层），给controller层的类提供接口进行调用。一般就是自己写的方法封装起来，声明一下，具体实现在serviceImpl 中
 * controller层（web层），控制层，负责具体模块的业务流程控制，需要调用service逻辑设计层的接口来控制业务流程，因为service中的方法是我们使用到的，controller通过接收前端H5或者App传过来的参数进行业务操作，再将处理结果返回到前端。
 */
@Slf4j
@SpringBootApplication
//开启组件扫描,，来扫描webfilter注解，从而创建过滤器
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching //开启spring-cache的缓存注解功能
public class ReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class,args);
        log.info("project is running...");
    }

}
