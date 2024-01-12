package com.yzx.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 * @Data是Lombok库中的一个注解，它可以帮助我们自动生成实体类的getter和setter方法，以及equals、hashCode和toString方法。使用@Data注解可以简化实体类的编写，减少样板代码的编写量。
 * https://developer.aliyun.com/article/1055552
 * 很多人可能疑惑🤔，你这明明啥都没干，怎么就实现了雪花算法生成Id。
 * 其实mybatis-plus已经内置雪花算法生成分布式唯一id。
 * 在mybatis-plus特性中已经明确说明了这点。
 * id呈现雪花算法主键自增
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
//    @JsonSerialize(Using=ToStringSerializer.class)
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber; //对应到表里是id_number,配置里开启了camel-case

    private Integer status;

    @TableField(fill = FieldFill.INSERT) //插入时填充字段，指定策略
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新的时候填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
