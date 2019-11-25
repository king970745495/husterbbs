package com.huster.bbs.service;

import com.huster.bbs.dao.CommentDAO;
import com.huster.bbs.model.Comment;
import com.huster.bbs.utils.BBSUtil;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    JedisAdapter jedisAdapter;

    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDAO.selectCommentByEntity(entityId, entityType);
    }

    public int addComment(Comment comment) {
        //过滤敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));

        //更新问题分数【现在问题的分数与创建的时间+问题的评论数有关】
        //更新redis的数据,存入redis的zset中，评论的数量+1
        jedisAdapter.zadd(RedisKeyUtil.getQuestionCommentCountKey()
                ,jedisAdapter.zscore(RedisKeyUtil.getQuestionCommentCountKey(),String.valueOf(comment.getEntityId())) + 1
                , String.valueOf(comment.getEntityId()));
        //存入问题的分数zset中，分数由更新后的评论数与问题的id组合而成
        jedisAdapter.zadd(RedisKeyUtil.getQuestionScoreKey(), BBSUtil.getQuestionScores(comment.getEntityId()
                , jedisAdapter.zscore(RedisKeyUtil.getQuestionCommentCountKey(), String.valueOf(comment.getEntityId()))), String.valueOf(comment.getEntityId()));

        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId,entityType);
    }

    //删除评论
    public boolean deleteComment(int commentId) {
        return commentDAO.updateStatus(commentId, 1) > 0;
    }

    //根据id查询comment
    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }



}
