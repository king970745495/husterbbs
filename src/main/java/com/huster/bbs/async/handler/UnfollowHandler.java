package com.huster.bbs.async.handler;

import com.huster.bbs.async.EventHandler;
import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.EntityType;
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
public class UnfollowHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHodler hostHodler;

    @Override
    public void doHandle(EventModel model) {
        // 给关注者发私信
        int fromId=BBSUtil.ADMIN_USERID;
        int toId = model.getEntityOwnerId();
        Message message = new Message();
        message.setFromId(BBSUtil.ADMIN_USERID);
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
        User user = userService.getUser(model.getActorId());

        if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName() + "取消关注了你的问题，http://www.husterbbs/question/" + model.getEntityId());
        } else if(model.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("用户" + user.getName() + "取消关注了你，http://www.husterbbs/question/" + model.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.UNFOLLOW);//取消关注的事件
    }
}
