package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzx.reggie.common.R;
import com.yzx.reggie.entity.Employee;
import com.yzx.reggie.service.EmployeeServcie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")

public class EmployeeController {
    /**
     * 当在controller层声明service接口变量时，通常需要使用@Autowired或@Resource注解来注入对应的service接口实现类。
     * 这样可以在controller中直接调用service接口中定义的方法来处理业务逻辑。
     */

    //定义一个接口类型的变量，用具体实现类赋值,有这个注解，spring IOC会自动将这个接口类的实现类对象赋值给接口属性
    //古老的方法private EmployeeServcie employeeServciee = new EmployeeServcie();手动创建，这样的话每个类都必须明确地创建它需要的其他类的实例，并且在代码中显式地引用这些实例。
    @Autowired
    private EmployeeServcie employeeServcie;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    //@RequestBody获取json数据
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //由于要操作session,所以需要加上request对象，用于获取session
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //封装查询规范，根据用户名等值查询，这里的queryWrapper类似于一个查询条件构造器
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //employee表name是唯一索引，所以可以用getone
        Employee emp = employeeServcie.getOne(queryWrapper);
        if (emp == null) {
            return R.error("登录失败");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //拓展了mvc的对象转换器之后，项目启动的时候
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 新增员工
     * 1.前端页面发送ajax请求
     * 2.服务端controller接受页面提交的数据，并调用service将数据进行保存
     * 3.service调用Mapper操作数据库，保存数据
     */


    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息: {}", employee.toString());

        //设置初使密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
//        long empid = (long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);

        employeeServcie.save(employee);
        return R.success("新增员工成功");

    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    //这里泛型不能用employee了，因为传来字段里面没有,要用mybatis-plus提供的page类’
    //底层是依靠mybatisplus提供的分页插件来进行分页查询
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        if (name != "" && name != null) {
            queryWrapper.like(Employee::getName, name);
        }
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeServcie.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //1739219905577037800(Long),这里前端传来的id字段出现了精度丢失，js只支持16位以内的，这里19位，超出的部分四舍五入了，导致我们根据前端传来的id去数据库里查不到，实际是1739219905577037826
        //这里我们可以在服务端给页面响应json数据时进行处理，将long型数据统一转换成String字符串
        /**
         * 解决方法1:在entity实体类的empId字段上加上注解@JsonSerialize(Using=ToStringSerializer.class)
         * 解决方法2:在服务端给前端响应json数据时，会使用springmvc的组件消息转换器，我们需要在配置类里去扩展Spingmvc的消息转换器，在此消息转换器中使用提供的对象转换器进行java 对象到json数据的转换。
         * 那么消息转换器在转json时还会调用对象转换器JacksonObjectMapper,基于jackson进行java对象转json数据的转换
         * */
//        System.out.println(employee.getId().getClass().toString());
        System.out.println(employee.getId()+1);
        //当加完对象转化器之后，服务端将long转换为string
        //使用了@TableField后，这些公共字段由统一填充了，就不需要在controller里在写了
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeServcie.updateById(employee);
        long id = Thread.currentThread().getId();
        log.info("线程id为{}",id);
        return R.success("员工信息修改完成");
    }
    //参数id在path里
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeServcie.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工");
    }
}