package com.huster.bbs.controller;

import com.huster.bbs.model.HostHodler;
import com.huster.bbs.model.ViewObject;
import com.huster.bbs.model.Question;
import com.huster.bbs.model.User;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 新的首页
 */
@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @Autowired
    HostHodler hostHodler;

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    //私有方法，用于根据条件查询问题，并将问题包装成ViewObject
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<ViewObject> vos = new ArrayList<ViewObject>();

        List<Question> questions = questionService.getLatestQuestion(userId,offset,limit);
        for (Question question : questions) {//对于查询到的每个问题，都需要将问题信息+用户信息  包装进ViewObject，然后传递至前端页面
            ViewObject obj = new ViewObject();
            obj.set("question", question);
            User user = userService.getUser(question.getUserId());
            obj.set("user", user);
            vos.add(obj);
        }
        return vos;
    }

    @RequestMapping(path = {"/index","/"}, method ={RequestMethod.GET,RequestMethod.POST})
    public String index(Model model) {
        model.addAttribute("vos", getQuestions(0,0,10));
        return "index";
    }
    @RequestMapping(path = {"/user/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @PathVariable("id") int id) {
        model.addAttribute("vos", getQuestions(id,0,10));
        return "index";
    }

}
