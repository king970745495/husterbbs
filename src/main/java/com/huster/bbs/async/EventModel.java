package com.huster.bbs.async;

import java.util.HashMap;
import java.util.Map;

/**
 * 事件的模型,记录了事件发生的现场
 */
public class EventModel {
    private EventType type;
    private int actorId;//触发事件的人
    private int entityType;//触发事件的类型
    private int entityId;//触发事件的id
    private int entityOwnerId;//被触发的实体的拥有者，即事件需要通知的人

    //像viewObject一样，存储一些额外的信息
    private Map<String, String> exts = new HashMap<>();

    public EventModel() {

    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }

    public String getExt(String key) {
        return exts.get(key);
    }

    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;//为了后方书写方便，可以使用xx.setType().setXX().setXX()可以一次性把所有的属性设置
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
