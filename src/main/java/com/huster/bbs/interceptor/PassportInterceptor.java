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
 * 功能：在用户每个访问请求最开始，验证用户身份
 * 验证用户身份的拦截器，根据cookie判断用户是否已登录，若已登录则将用户身份设置为一个Threadlocal中的线程独有变量，在controller访问的前中后进行拦截
 */
//这个controller一定要加
@Controller
@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    HostHodler hostHodler;

    //拦截验证用户身份
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //处于所有http请求的最前面，先根据cookie判断用户是否已登录
        String ticket = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {//查询cookie中有没有ticket
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        //判断ticket是否过期了
        /**
         * 这里可以改进成使用redis进行记录
         */
        if (ticket != null) {
            LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
            if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0) {
                return true;//表明过期了，就不用将用户信息加入共享的bean中
            }
            User user = userDAO.getUser(loginTicket.getUserId());
            hostHodler.setUser(user);
        }
        return true;
    }

    //验证用户身份后，页面渲染前，将user的信息加入model
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {//在页面渲染前执行，将user对象加入model的域中，那么所有选然后的页面就可以直接在属性中访问user
            modelAndView.addObject("user",hostHodler.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在渲染完页面后，将存储的user信息清空，只要cookie中还带有ticket就可以实现user信息加入modelandView中
        hostHodler.clear();
    }
}
