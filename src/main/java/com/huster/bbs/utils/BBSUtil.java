package com.huster.bbs.utils;

import com.alibaba.fastjson.JSONObject;
import com.huster.bbs.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/3.
 */
public class BBSUtil {
    private static final Logger logger = LoggerFactory.getLogger(BBSUtil.class);

    //匿名游客的id
    public static  int ANONYMOUS_USERID = 0;
    //系统管理员id
    public static  int ADMIN_USERID = 1;
    //计算问题分数的参数
    public static double G = 1.8;

    /**
     * 处理json数据的方法
     * @param code 数据是否正确
     * @param msg 数据传递的数据（消息）
     * @return json字符串
     */
    public static String getJsonString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg",msg);
        return json.toJSONString();
    }
    // Json返回格式封装,所有返回json前端js都有对应处理
    public static String getJsonString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }
    public static String getJsonString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }


    /**
     * 对字符串进行加密解密，比如用到对密码的加密中
     * @param key
     * @return
     */
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            logger.error("生成MD5失败", e);
            return null;
        }
    }

//    public static double getQuestionScores(Question question, Double p) {
    public static double getQuestionScores(int questionId, Double p) {//p为评论数
        /*double t = (int)((System.currentTimeMillis() - question.getCreatedDate().getTime())/3600000);//问题发布时间到现在的小时数
        //int p = question.getCommentCount();
        double score = (p - 1)/Math.pow(t + 2, G);
        return score;*/
        //两种刷新分数的解决方案：
        // 1.分数与评论、发布问题相关，有相关的事件时触发相应的动作，来刷新分数
        // 2.以后可以考虑单独开一个线程，专门刷新redis中的排行榜，多长时间刷新一次，redis中存储三个zset，一个保存起始时间，一个保存评论数，一个保存分数
        double score = p+Math.log(questionId);
        return score;//现在暂时用这个机制，log减缓问题的上升速度
    }



}
