package com.huster.bbs.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.huster.bbs.async.EventHandler;
import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.*;
import com.huster.bbs.service.*;
import com.huster.bbs.utils.BBSUtil;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 点赞事件的处理器
 */
@Component
public class FeedHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHodler hostHodler;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void doHandle(EventModel model) {
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setUserId(model.getActorId());
        feed.setType(model.getType().getValue());
        feed.setData(buildFeedData(model));//data需要转存成json格式进行存储
        if (feed.getData() == null) return;

        //将这个博文事件加入feed总表中
        feedService.addFeed(feed);

        //给事件的所有粉丝，推送这个博文
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);//取出所有的粉丝
        followers.add(0);//0代表系统，即给所有的粉丝发完，也要给系统发送一个博文通知
        for (int follower : followers) {//所有的粉丝的timeLine（个性化显示的时间轴），都会将这个博文通知的id加入list
            String timeLineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timeLineKey, String.valueOf(feed.getId()));
        }

    }

    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<>();
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());
        // 当评论一个问题或关注一个问题（不考虑关注人）的时候
        if (model.getType() == EventType.COMMENT || model.getType() == EventType.QUESTION || (model.getType() == EventType.FOLLOW && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getQuestionById(model.getEntityId());
            if (question == null) {
                return null;
            }
            // 往map里装问题信息
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,  EventType.QUESTION});//提问、评论的事件，产生特别关心的动态  EventType.FOLLOW，暂时不关心
    }
}
