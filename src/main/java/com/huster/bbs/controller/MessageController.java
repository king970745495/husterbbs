package com.huster.bbs.controller;

import com.huster.bbs.model.HostHodler;
import com.huster.bbs.model.Message;
import com.huster.bbs.model.User;
import com.huster.bbs.model.ViewObject;
import com.huster.bbs.service.MessageService;
import com.huster.bbs.service.UserService;
import com.huster.bbs.utils.BBSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    HostHodler hostHodler;
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;


    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(value = "/msg/list", method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        if (hostHodler.getUser() == null) {
            return "redirect:/relogin";
        }
        int localUserId = hostHodler.getUser().getId();
        List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
        //将模型数据，封装成前端页面中一行一行所需的ViewObject数据
        List<ViewObject> conversations = new ArrayList<>();
        for (Message message : conversationList) {
            ViewObject vo = new ViewObject();
            vo.set("message", message);
            //得到消息的对方id
            int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
            vo.set("user", userService.getUser(targetId));
            vo.set("unread", messageService.getConversationUnreadCount(localUserId, message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations", conversations);
        return "letter";
    }

    @RequestMapping(value = "/msg/detail", method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @RequestParam("conversationId") String conversationId) {

        try {
            messageService.updateConversationStatus(conversationId, hostHodler.getUser().getId());//首先将消息的状态改为已读
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> message = new ArrayList<>();//将站内信封装成ViewObject，然后在前端遍历时，每一个板块内使用
            for (Message msg : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                vo.set("user", userService.getUser(msg.getFromId()));
                message.add(vo);
            }
            model.addAttribute("messages", message);
        } catch (Exception e) {
            logger.error("获取详情失败" + e.getMessage());
        }
        return "letterDetail";
    }

    /**
     * 发送消息的处理器
     * @param toName
     * @param content
     * @return 由于前端是ajax请求，因此需要返回一个json数据，999代表未登录，0正确，1代表错误
     */
    @RequestMapping(value = "/msg/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try {
            if (hostHodler.getUser() ==  null) {
                return BBSUtil.getJsonString(999, "未登录");
            }
            User user = userService.getUserByName(toName);
            if (user ==  null) {
                return BBSUtil.getJsonString(1, "用户不存在");
            }

            int fromId = hostHodler.getUser().getId();
            int toId = user.getId();
            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(fromId);
            message.setToId(toId);
            message.setContent(content);
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            return BBSUtil.getJsonString(0);

        } catch (Exception e) {
            logger.error("发送消息失败" + e.getMessage());
            return BBSUtil.getJsonString(1, "发送信息失败");
        }
    }


}
