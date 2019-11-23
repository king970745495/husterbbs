package com.huster.bbs.async.handler;

import com.huster.bbs.async.EventHandler;
import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.*;
import com.huster.bbs.service.MessageService;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.UserService;
import com.huster.bbs.utils.BBSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 点赞事件的处理器
 */
@Component
public class CommentHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHodler hostHodler;

    @Autowired
    QuestionService questionService;

    @Override
    public void doHandle(EventModel model) {
        // 给被评论者发私信
        int fromId=BBSUtil.ADMIN_USERID;

        Question question = questionService.getQuestionById(model.getEntityId());
        int toId = question.getUserId();
        Message message = new Message();
        message.setFromId(BBSUtil.ADMIN_USERID);
        message.setToId(toId);
        message.setCreatedDate(new Date());
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
        User user = userService.getUser(model.getActorId());

        /*if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName() + "关注了你的问题，http://www.husterbbs/question/" + model.getEntityId());
        } else if(model.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("用户" + user.getName() + "关注了你，http://www.husterbbs/question/" + model.getActorId());
        }*/
        message.setContent("用户" + user.getName() + "关注了你的问题：" + question.getTitle() + "http://www.husterbbs/question/" + question.getId());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.COMMENT);//评论事件
    }
}
