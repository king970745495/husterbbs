package com.huster.bbs.controller;

import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventProducer;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.*;
import com.huster.bbs.service.CommentService;
import com.huster.bbs.service.FollowService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新的首页
 */
@Controller
public class FollowController {

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    HostHodler hostHodler;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;


    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(FollowController.class);


    @RequestMapping(path = {"/followUser"}, method ={RequestMethod.POST})
    @ResponseBody
    public String followUser(int userId) {//现在在登录的用户，关注userId的用户
        if (hostHodler.getUser() == null) {//未登录，返回登录页面
            return BBSUtil.getJsonString(999);
        }
        boolean ret = followService.follow(hostHodler.getUser().getId(), EntityType.ENTITY_USER, userId);
        //发送关注事件的消息（某用户关注了另一用户，系统向被关注的用户发送一条消息）,new EventModel(EventType.FOLLOW)表示发出一个follow事件
        //哪种事件，由哪个人，向哪种实体类型的哪个实体发送（实体的拥有者是谁，即消息需要发给谁），发送了什么附加信息
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHodler.getUser().getId())
                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwnerId(userId));//关注某个人，这个人的拥有者，就是userId
        //返回当前用户关注的人的总数
        return BBSUtil.getJsonString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHodler.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/unfollowUser"})
    @ResponseBody
    public String unfollowUser(int userId) {//现在在登录的用户，关注userId的用户
        if (hostHodler.getUser() == null) {//未登录，返回登录页面
            return BBSUtil.getJsonString(999);
        }
        boolean ret = followService.unfollow(hostHodler.getUser().getId(), EntityType.ENTITY_USER, userId);
        //发送关注事件的消息（某用户关注了另一用户，系统向被关注的用户发送一条消息）,new EventModel(EventType.FOLLOW)表示发出一个follow事件
        //哪种事件，由哪个人，向哪种实体类型的哪个实体发送（实体的拥有者是谁，即消息需要发给谁），发送了什么附加信息
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHodler.getUser().getId())
                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwnerId(userId));//关注某个人，这个人的拥有者，就是userId
        //返回当前用户关注的人的总数
        return BBSUtil.getJsonString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(hostHodler.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(path = {"/followQuestion"})
    @ResponseBody
    public String followQuestion(int questionId) {//现在在登录的用户，关注questionId
        if (hostHodler.getUser() == null) {//未登录，返回登录页面
            return BBSUtil.getJsonString(999);
        }
        //判断问题是否存在
        Question q = questionService.getQuestionById(questionId);
        if (q == null) {
            return BBSUtil.getJsonString(1, "问题不存在");//1表示错误的代码，0表示正确，999表示未登录
        }
        //关注问题
        boolean ret = followService.follow(hostHodler.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        //发送关注事件的消息（某用户关注了某一问题，系统向被关注的用户发送一条消息）,new EventModel(EventType.FOLLOW)表示发出一个follow事件
        //哪种事件，由哪个人，向哪种实体类型的哪个实体发送（实体的拥有者是谁，即消息需要发给谁），发送了什么附加信息
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHodler.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setEntityOwnerId(q.getUserId()));//关注某个人，这个人的拥有者，就是userId
        //返回前端显示所需的数据

        // 发送用户本人信息用于页面展示
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHodler.getUser().getHeadUrl());
        info.put("name", hostHodler.getUser().getName());
        info.put("id", hostHodler.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return BBSUtil.getJsonString(ret ? 0 : 1, info);
    }

    @RequestMapping(path = {"/unfollowQuestion"})
    @ResponseBody
    public String unfollowQuestion(int questionId) {//现在在登录的用户，关注questionId
        if (hostHodler.getUser() == null) {//未登录，返回登录页面
            return BBSUtil.getJsonString(999);
        }
        //判断问题是否存在
        Question q = questionService.getQuestionById(questionId);
        if (q == null) {
            return BBSUtil.getJsonString(1, "问题不存在");//1表示错误的代码，0表示正确，999表示未登录
        }
        //关注问题
        boolean ret = followService.unfollow(hostHodler.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
        //发送关注事件的消息（某用户关注了某一问题，系统向被关注的用户发送一条消息）,new EventModel(EventType.FOLLOW)表示发出一个follow事件
        //哪种事件，由哪个人，向哪种实体类型的哪个实体发送（实体的拥有者是谁，即消息需要发给谁），发送了什么附加信息
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHodler.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId).setEntityOwnerId(q.getUserId()));//关注某个人，这个人的拥有者，就是userId
        // 发送用户本人信息用于页面展示
        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHodler.getUser().getHeadUrl());
        info.put("name", hostHodler.getUser().getName());
        info.put("id", hostHodler.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return BBSUtil.getJsonString(ret ? 0 : 1, info);
    }

    @GetMapping(value = "/user/{uid}/followers")
    public String followers(Model model, @PathVariable("uid") int userId) {
        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
        if (hostHodler.getUser() != null) {
            model.addAttribute("followers", getUsersInfo(hostHodler.getUser().getId(), followerIds));
        } else {
            model.addAttribute("followers", getUsersInfo(0, followerIds));
        }
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followers";
    }

    @GetMapping(value = "/user/{uid}/followees")
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees(userId, EntityType.ENTITY_USER, 0, 10);

        if (hostHodler.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHodler.getUser().getId(), followeeIds));
        } else {//如果未登录，获取当前用户关注者的信息时，不显示能不能关注的按钮。
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }

    private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> userInfos = new ArrayList<>();
        for (Integer uid : userIds) {
            User user = userService.getUser(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentCount(uid));
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));
            vo.set("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));
            if (localUserId != 0) {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("followed", false);
            }
            userInfos.add(vo);
        }
        return userInfos;
    }


}
