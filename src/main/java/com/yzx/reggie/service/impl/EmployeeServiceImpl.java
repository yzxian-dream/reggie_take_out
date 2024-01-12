package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.entity.Employee;
import com.yzx.reggie.mapper.EmployeeMapper;
import com.yzx.reggie.service.EmployeeServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
//@Service用于标记服务层Bean的注解，当spring应用启动时，该Bean会被自动创建并加入到Spring应用上下文，在@autowried时候注入它的实例。即可调用它的方法
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeServcie {

}
