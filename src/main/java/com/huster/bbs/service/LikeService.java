package com.huster.bbs.service;

import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

/**
 * 点赞的服务
 */

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    //得到这个问题/评论喜欢的人数
    public long getLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    //得到某个用户是否喜欢这个问题/评论
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;//用户点赞了这个问题
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;//踩赞返回-1，否则没踩也没点赞，返回0
    }

    //某个用户对某个问题/回答，点赞，返回点赞人数
    public long like(int userId, int entityType, int entityId) {
        //加入这个问题的like集合中
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);//首先要得到这个type下的这个id的存储set集合的key，这里用一个专门的工具类生成key，实现一致
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        //将dislike集合中的这个userid移除，就算最开是dislike集合中没有，也不会报错，redis只会返回0
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        //返回给这个问题点赞的集合中的人数
        return jedisAdapter.scard(likeKey);
    }

    //踩赞后，返回点赞的人数
    public long disLike(int userId, int entityType, int entityId) {
        // 从不喜欢的集合里添加这个userId
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        // 从喜欢的集合里删除这个userId
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        // 返回喜欢人数
        return jedisAdapter.scard(likeKey);
    }




}
