package com.huster.bbs.model;

import org.springframework.stereotype.Component;

@Component
public class HostHodler {
    //用于用户登录后，各服务之间共享用户的信息
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }

}
