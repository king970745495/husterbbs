package com.huster.bbs.controller;

import com.huster.bbs.model.*;
import com.huster.bbs.service.CommentService;
import com.huster.bbs.service.FollowService;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.UserService;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHodler hostHodler;
    @Autowired
    JedisAdapter jedisAdapter;

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    //私有方法，用于根据条件查询问题，并将问题包装成ViewObject
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        Set<String> questionIds = jedisAdapter.zrevrange(RedisKeyUtil.getQuestionScoreKey(), offset, limit);
//        Set<String> questionIds = jedisAdapter.zrange(RedisKeyUtil.getQuestionScoreKey(), offset, limit);
        List<Integer> ids = new ArrayList<>();
        for (String id : questionIds) {
            int i = Integer.parseInt(id);
            ids.add(i);
        }
        List<ViewObject> vos = new ArrayList<ViewObject>(Arrays.asList(new ViewObject[ids.size()]));
        List<Question> questions = questionService.getQuestionsByIds(ids);
        //List<Question> questions = questionService.getLatestQuestion(userId,offset,limit);
        for (Question question : questions) {//对于查询到的每个问题，都需要将问题信息+用户信息  包装进ViewObject，然后传递至前端页面
            ViewObject obj = new ViewObject();
            question.setCommentCount(jedisAdapter.zscore(RedisKeyUtil.getQuestionCommentCountKey(), String.valueOf(question.getId())).intValue());
            obj.set("question", question);
            User user = userService.getUser(question.getUserId());
            obj.set("user", user);
//            vos.add();
            vos.set(ids.indexOf(question.getId()), obj);
            /*if (question.getUserId() == hostHodler.getUser().getId()) {

            }*/

        }
        return vos;
    }
    private List<ViewObject> getAllQuestions(int userId) {
        Set<String> questionIds = jedisAdapter.zrevrange(RedisKeyUtil.getQuestionScoreKey(), 0, -1);
//        Set<String> questionIds = jedisAdapter.zrange(RedisKeyUtil.getQuestionScoreKey(), offset, limit);
        List<Integer> ids = new ArrayList<>();
        for (String id : questionIds) {
            int i = Integer.parseInt(id);
            ids.add(i);
        }
        List<ViewObject> vos = new ArrayList<ViewObject>(Arrays.asList(new ViewObject[ids.size()]));
        List<Question> questions = questionService.getQuestionsByIds(ids);

        //List<Question> questions = questionService.getQuestions(userId);
        for (Question question : questions) {//对于查询到的每个问题，都需要将问题信息+用户信息  包装进ViewObject，然后传递至前端页面
            ViewObject obj = new ViewObject();
            obj.set("question", question);
            User user = userService.getUser(question.getUserId());
            obj.set("user", user);
//            vos.add(obj);
            vos.set(ids.indexOf(question.getId()), obj);
        }
        return vos;
    }

    @RequestMapping(path = {"/index","/"}, method ={RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, HttpServletRequest request) {
        if (request.getParameter("more") != null) {
            model.addAttribute("vos", getAllQuestions(0));
            return "index";
        }
        model.addAttribute("vos", getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @PathVariable("id") int userId) {
        model.addAttribute("vos", getQuestions(userId,0,10));
        // 显示关注和被关注列表
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        if (hostHodler.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHodler.getUser().getId(), userId, EntityType.ENTITY_USER));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
//        return "index";
        return "profile";
    }

}
