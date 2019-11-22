package com.huster.bbs.async;

import com.alibaba.fastjson.JSONObject;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 事件生产者
 */
@Service
public class EventProducer {

    @Autowired
    JedisAdapter jedisAdapter;

    //将事件推入队列
    public boolean fireEvent(EventModel eventModel) {
        try {
            String json = JSONObject.toJSONString(eventModel);//利用json实现序列化与反序列化
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

}
