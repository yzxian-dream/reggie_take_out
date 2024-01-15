package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yzx.reggie.common.R;
import com.yzx.reggie.config.RedisConfig;
import com.yzx.reggie.entity.User;
import com.yzx.reggie.service.UseService;
import com.yzx.reggie.utils.SMSUtils;
import com.yzx.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UseService useService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 处理移动端业务，调用阿里云提供的sdk调用阿里云的api进行短信发送业务
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.hasText(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成短信发送
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存session,到时候比对使用
//            session.setAttribute(phone,code);

            redisTemplate.opsForValue().set(phone,code, 5,TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");
        }
        return R.error("手机验证码短信发送失败");
    }

    @PostMapping("/login")
    //这里可以新建dto接收也可以用map接收
    public R<User> login(@RequestBody Map map,HttpSession session){
        log.info(map.toString());
        //获取页面提供的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis中获取
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        //验证码比对

        if (codeInSession != null && codeInSession.equals(code)){
            //比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = useService.getOne(queryWrapper);
            //判断当前手机号对应的用户是否为新用户，如果是新用户，就自动帮用户注册，也就是把这个手机号存入user表，对用户来说是无感知的
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                useService.save(user);
            }
            //Session位于服务器端，sessionStorage位于客户端，
            //前端的代码sessionStorage.setItem("userPhone",this.form.phone)，这个在F12里的sessionStorage能看见，session.setAttribute("user",user.getId())不在
            /**
             * 二.两者区别总结：
             * （1）sessionStorage存储在客户端，Session在服务器端。
             * （2）Session主要用户维护会话状态。
             * （3）sessionStorage则是在会话期间存储相关数据。
             * 但是Session与sessionStorage会话周期是不同的，下面简单介绍如下：
             * （1）关闭浏览器或者服务器端Session过期，会话结束。
             * （2）关闭当前选项卡或者浏览器窗口，sessionStorage数据被删除，也就算会话结束。
             */
            session.setAttribute("user",user.getId());
            log.info(session.getAttribute("user").toString());
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");




    }

}
