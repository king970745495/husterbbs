package com.huster.bbs.async;

/**
 * 事件的类型
 */
public enum EventType {
    //表明是什么事件
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    QUESTION(6);

    //构造函数
    private int value;
    EventType(int value) { this.value = value; }
    public int getValue() { return value; }

}
