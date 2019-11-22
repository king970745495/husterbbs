package com.huster.bbs.async.handler;

import com.huster.bbs.async.EventHandler;
import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.HostHodler;
import com.huster.bbs.model.Message;
import com.huster.bbs.model.User;
import com.huster.bbs.service.MessageService;
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
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHodler hostHodler;

    @Override
    public void doHandle(EventModel model) {
        /*if (model.getActorId() != hostHodler.getUser().getId()) {

        }*/
        //点赞了以后，给被点赞的用户发送一条消息
        Message message = new Message();
        message.setFromId(BBSUtil.ADMIN_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName() + "点赞了你的评论，http://www.husterbbs/question/" + model.getExt("questionId"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);//关注like的事件
    }
}
