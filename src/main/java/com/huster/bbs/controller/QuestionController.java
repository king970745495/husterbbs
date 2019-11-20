package com.huster.bbs.controller;

import com.alibaba.fastjson.JSONObject;
import com.huster.bbs.model.*;
import com.huster.bbs.service.CommentService;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.UserService;
import com.huster.bbs.utils.BBSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 处理提问业务的controller
 */

@Controller
public class QuestionController {

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @Autowired
    HostHodler hostHodler;
    @Autowired
    CommentService commentService;

    /**
     * 处理新增问题的ajax请求
     * @param title 问题标题
     * @param content 问题内容
     * @return 请求响应的页面
     */
    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setCommentCount(0);
            question.setContent(content);
            question.setCreatedDate(new Date());
            question.setTitle(title);
            if (hostHodler.getUser() == null) {//根据线程局部变量判断用户是否已经登录
                //question.setId(BBSUtil.ANONYMOUS_USERID);//普通用户要想不登陆发布问题，就使用一个匿名的用户（id：3）进行发布
                //实现与前端中得JavaScript交互，如果用户未登录，就置code=999(popupAdd.js)
                return BBSUtil.getJsonString(999);
            } else {
                question.setUserId(hostHodler.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return BBSUtil.getJsonString(0);
            }
        } catch (Exception e) {
            logger.error("发布问题失败" + e.getMessage());
        }
        return BBSUtil.getJsonString(1,"失败");
    }

    /**
     * 显示一个问题详情
     * @param model
     * @param id
     * @return
     */
    @RequestMapping(value = "/question/{id}")
    public String getQuestionDetail (Model model, @PathVariable(value = "id") int id) {
        Question question = questionService.getQuestionById(id);
        model.addAttribute("question", question);
        /*User user = userService.getUser(question.getId());
        model.addAttribute("user", user);*/

        List<Comment> commentList = commentService.getCommentsByEntity(id, EntityType.ENTITY_QUESTION);

        List<ViewObject> comments = new ArrayList<ViewObject>();//将评论的所需材料封装在一个对象中，在前端可以实现对这个对象集合的遍历，实现列举所有的评论
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            vo.set("user", userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments", comments);
        return "detail";
    }

}
