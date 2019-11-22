package com.huster.bbs.async;

import java.util.List;

/**
 * 事件处理器的接口，定义为接口后，可以定义接口的不同的实现类，来处理不同类型的事件。
 */
public interface EventHandler {
    // 处理event的方法
    void doHandle(EventModel model);
    // 哪些event类型被关注
    List<EventType> getSupportEventTypes();
}