package com.huster.bbs.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huster.bbs.utils.BBSUtil;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件的消费者
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    // 通过map实现的最简单的消息分发，记录某种EventType事件，需要触发的EventHandler
    private Map<EventType, List<EventHandler>> config = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //找到eventHandler所有的实现类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //得到每个EventHandler支持的EventType
                List<EventType> supportEventTypes = entry.getValue().getSupportEventTypes();
                //得到每个EventType会触发的EventHandler
                for (EventType type : supportEventTypes) {
                    if (!config.containsKey(type)) {
                        List<EventHandler> list = new ArrayList<>();
                        list.add(entry.getValue());
                        config.put(type, list);
                    } else {
                        config.get(type).add(entry.getValue());
                    }
                }
            }
        }

        //开启另一线程，消费事件队列里的事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    //获取redis中以key为键的单向队列中最先入队的事件
                    List<String> events = jedisAdapter.brpop(0, key);
                    for (String message : events) {
                        //brpop的第一个值是key，直接过滤掉
                        if (message.equals(key)) {
                            continue;
                        }
                        //解析message
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            System.out.println(eventModel.getType());
                            logger.error("无法识别的事件");
                            continue;
                        }
                        List<EventHandler> handlers = config.get(eventModel.getType());
                        for (EventHandler handler : handlers) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
