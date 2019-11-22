package com.huster.bbs.controller;

import com.huster.bbs.async.EventModel;
import com.huster.bbs.async.EventProducer;
import com.huster.bbs.async.EventType;
import com.huster.bbs.model.Comment;
import com.huster.bbs.model.EntityType;
import com.huster.bbs.model.HostHodler;
import com.huster.bbs.service.CommentService;
import com.huster.bbs.service.LikeService;
import com.huster.bbs.utils.BBSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞处理器
 */

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHodler hostHodler;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like (Model model, @RequestParam("commentId") int commentId) {
        if (hostHodler.getUser() == null) {
            return BBSUtil.getJsonString(999);
        }
        // 获取点赞的那条评论
        Comment comment = commentService.getCommentById(commentId);
        if (hostHodler.getUser().getId() != comment.getUserId()) {
            eventProducer.fireEvent(new EventModel(EventType.LIKE)
                    .setActorId(hostHodler.getUser().getId())
                    .setEntityId(commentId)
                    .setEntityType(EntityType.ENTITY_COMMENT)
                    .setEntityOwnerId(comment.getUserId())
                    .setExt("questionId", String.valueOf(comment.getEntityId())));//为了生成具体问题页的链接
        }

        // 返回前端点赞数
        long likeCount = likeService.like(hostHodler.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return BBSUtil.getJsonString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(Model model, @RequestParam("commentId") int commentId) {
        if (hostHodler.getUser() == null) {
            return BBSUtil.getJsonString(999);
        }
        // 获取踩赞的那条评论
        Comment comment = commentService.getCommentById(commentId);
        // 返回前端点赞数
        long likeCount = likeService.disLike(hostHodler.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return BBSUtil.getJsonString(0, String.valueOf(likeCount));

    }


}
