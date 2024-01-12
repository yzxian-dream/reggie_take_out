package com.yzx.reggie.filter;

import com.mysql.cj.xdevapi.JsonString;
import com.yzx.reggie.common.BaseContext;
import com.yzx.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.alibaba.fastjson.JSON;


/**
 * 请求拦截器，用来解决有些用户并没有登录而去直接访问一下静态页面出现的问题，
 */
//指定过滤器，需要拦截哪些路径
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    /**
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;//向下转型
        //获取本次访问的url，判断是否需要处理，不需要放行，否则处理，判断登录状态
        //{}代表占位符
//        log.info("拦截请求: {}", request.getRequestURI());
        //定义可以放行的url
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        String requestURI = request.getRequestURI();
        boolean check = check(urls, requestURI);
        if (check){
            //不需要处理，直接放行
            filterChain.doFilter(request, response);
            return;
        }
        //判断登录状态，如果不匹配，判断是否登录，已登录直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为{}",id);
            filterChain.doFilter(request, response);
            return;
        }
        //判断移动端用户登录状态，如果不匹配，判断是否登录，已登录直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            long id = Thread.currentThread().getId();
            log.info("线程id为{}",id);
            filterChain.doFilter(request, response);
            return;
        }
        log.info("用户未登录");
        //如果未登录，通过输出流的方式向客户端页面响应数据。前端拿到数据后会自己处理跳转到login html
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            if (PATH_MATCHER.match(url,requestURI)){
//                log.info("拦截到的{}和{}匹配",requestURI,url );
                return true;
            }
//            log.info("拦截到的{}和{}不匹配",requestURI,url );
        }

        return false;
    }
}
