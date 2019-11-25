package com.huster.bbs.controller;

import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventProducer;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.Comment;
import com.huster.bbs.model.EntityType;
import com.huster.bbs.model.HostHodler;
import com.huster.bbs.model.Question;
import com.huster.bbs.service.CommentService;
import com.huster.bbs.service.FollowService;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.SensitiveService;
import com.huster.bbs.utils.BBSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Date;

@Controller
public class CommentController {

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHodler hostHodler;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    FollowService followService;

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHodler.getUser() != null) {
                comment.setUserId(hostHodler.getUser().getId());
            } else {
                comment.setUserId(BBSUtil.ANONYMOUS_USERID);
            }
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            comment.setStatus(0);
            commentService.addComment(comment);
            // 更新question的评论数
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(), count + 1);
            // 推送异步事件
            if (followService.getFollowerCount(EntityType.ENTITY_USER, hostHodler.getUser().getId()) < 100) {
                eventProducer.fireEvent(new EventModel(EventType.COMMENT).setActorId(comment.getUserId()).setEntityId(questionId));
            }

        } catch(Exception e) {
            logger.error("增加评论失败" + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }

}
