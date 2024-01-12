package com.yzx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yzx.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;

/**
 * 1.实体类
 * 2.Mapper接口
 * 3.Service接口
 * 4.Service实现类
 *
 * service实现类会实现service接口，并且继承ServiceImpl,里面使用的范型时对应的mapper和实体类
 */
@Mapper
//这里加上@mapper注释用来表示该接口类的实现类对象交由mybatis底层创建，然后交由spring框架管理。
// 至于mybatis底层如何实现这个类，类里的方法的具体实现应该是由mybatis操作mysql的底层来实现的，可以当作黑盒不去管
//这里也可以不用service层，那么在Controller层通过@autowried注入
public interface EmployeeMapper extends BaseMapper<Employee> {

}
