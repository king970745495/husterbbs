package com.huster.bbs.controller;

import com.huster.bbs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 注册处理器，处理注册的请求
 */
@Controller
public class LoginController {
    @Autowired
    UserService userService;

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    //注册请求处理
    @RequestMapping(path = "/reg", method = RequestMethod.POST)
    public String register(Model model,
                           @RequestParam(value = "username") String username,
                           @RequestParam(value = "password") String password,
                           @RequestParam(value = "remember", defaultValue = "false") boolean rememberme,
                           HttpServletResponse response) {
        try{
            Map<String, String> map = userService.register(username, password);//调用注册服务，检查用户名密码均没问题。
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                if (rememberme) cookie.setMaxAge(3600*24*5);
//                cookie.setDomain(".husterbbs.com");//这个操作是为了实现cookie跨域？这里不是分布式得，没有用taotao项目中的cookieUtils
                cookie.setPath("/");
                response.addCookie(cookie);
                //登录成功，返回首页
                return "redirect:/index";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e) {
            logger.error("注册异常",e.getMessage());
            return "login";
        }
    }

    //跳转到登录页面的请求【暂时测试用】
    @RequestMapping(path = "/relogin", method = RequestMethod.GET)
    public String regist(Model model) {
        return "login";
    }


    //登录请求处理
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try{
            Map<String, String> map = userService.login(username, password);//调用注册服务，检查用户名密码均没问题。
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                if (rememberme) cookie.setMaxAge(3600*24*5);
//                cookie.setDomain(".husterbbs.com");//这个操作是为了实现cookie跨域？这里不是分布式得,不用跨域，没有用taotao项目中的cookieUtils
                cookie.setPath("/");
                response.addCookie(cookie);
                //登录成功，返回首页
                return "redirect:/index";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e) {
            logger.error("登录异常",e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(Model model, HttpServletRequest request) {
        String ticket = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {//查询cookie中有没有ticket
                if (cookie.getName().equals("ticket")) {
                    ticket = cookie.getValue();
                    break;
                }
            }
            userService.logout(ticket);
            return "redirect:/index";
        }else {
            model.addAttribute("error", "没有cookie！");
            return "error";
        }

    }

}
