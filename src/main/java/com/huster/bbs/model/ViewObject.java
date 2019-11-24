package com.huster.bbs.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 负责向前台页面传递视图中数据的实体类
 */
public class ViewObject {

    private Map<String, Object> map;

    public Map<String, Object> getMap() {
//        if (map.size() == 0) return null;
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public ViewObject() {
        map = new HashMap<String, Object>();
    }

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }
}
