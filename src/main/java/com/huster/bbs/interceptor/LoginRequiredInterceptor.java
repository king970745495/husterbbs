package com.huster.bbs.interceptor;

import com.huster.bbs.dao.LoginTicketDAO;
import com.huster.bbs.dao.UserDAO;
import com.huster.bbs.model.HostHodler;
import com.huster.bbs.model.LoginTicket;
import com.huster.bbs.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 功能：用户未登录访问页面拦截
 * 对未登录的用户访问一些需要登录的页面时，进行拦截，跳转至登录页面后再跳转回来
 */
//这个controller一定要加
@Controller
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHodler hostHodler;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (hostHodler.getUser() ==  null) {
            response.sendRedirect("/relogin?next="+ request.getRequestURI());//将当前访问的页面作为参数传递到relogin页面
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
