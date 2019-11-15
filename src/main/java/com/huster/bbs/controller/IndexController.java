package com.huster.bbs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;

//@Controller
public class IndexController {

    @RequestMapping("/index/{id}")
    @ResponseBody
    public String index(@PathVariable int id,@RequestParam(defaultValue = "y") String type ,@RequestParam(value = "name",required = false) String name) {
        //return String.format("This is nowCode %d and %s",id,name);
        return id +" "+name+" "+type;
    }

    @RequestMapping(path = {"/vm"},method = {RequestMethod.GET})
    public String template(Model model) {
        model.addAttribute("name","king");

        ArrayList<String> list = new ArrayList<String>();
        list.add("hello");
        list.add("world");
        list.add("!!");
        model.addAttribute("array",list);

        return "home";
    }

    //测试controller里的参数的使用：
    @RequestMapping("/request")
    @ResponseBody
    public String request(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod() + "<br/>");
        sb.append(request.getRequestURI() + "<br/>");
        sb.append(request.getRequestedSessionId() + "<br/>");
        sb.append(request.getQueryString() + "<br/>");
        sb.append(request.getPathInfo() + "<br/>");

        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String header = request.getHeader(name);
            sb.append(name + ":" + header + "<br/>");
        }

        response.addHeader("name", "king");
        response.addCookie(new Cookie("username", "kingcookie"));

        return sb.toString();
    }

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    @RequestMapping("/admin/{id}")
    @ResponseBody
    public String isAdmin(@PathVariable int id) {
        logger.info("execute Method");
        if (id == 0) {
            return "Hello admin";
        }
        throw new IllegalArgumentException("非管理员登录");
    }


    @ExceptionHandler
    @ResponseBody
    public String exceptError(Exception e) {
        return "error:" + e.getMessage();
    }


}
