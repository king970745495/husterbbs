package com.huster.bbs.utils;

/**
 * 生成点赞功能的专用key
 */
public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    // 粉丝key的前缀
    private static String BIZ_FOLLOWER = "FOLLOWER";
    // 关注对象key的前缀
    private static String BIZ_FOLLOWEE = "FOLLOWEE";

    //新鲜事时间轴
    private static String BIZ_TIMELINE = "TIMELINE";

    //排序：redis中问题，分数-id    的zset
    private static String QUESTION_SCORE = "QUESTION_SCORE";
    //排序：redis中问题，评论数-id    的zset
    private static String QUESTION_COMMENT_COUNT = "QUESTION_COMMENT_COUNT";

    public static String getQuestionScoreKey() {
        return QUESTION_SCORE;
    }
    public static String getQuestionCommentCountKey() {
        return QUESTION_COMMENT_COUNT;
    }

    //点赞和踩赞功能的key
    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 获取消息队列的key
    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }

    // 获取某个实体，粉丝的key
    public static String getFollowerKey(int entityType, int entityId) {
        return BIZ_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 获取某个用户，关注某一类实体的key
    public static String getFolloweeKey(int userId, int entityType) {
        return BIZ_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    public static String getTimelineKey(int userId) {
        return BIZ_TIMELINE + SPLIT + userId;
    }


}
