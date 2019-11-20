package com.huster.bbs.service;

import com.huster.bbs.dao.LoginTicketDAO;
import com.huster.bbs.dao.UserDAO;
import com.huster.bbs.utils.BBSUtil;
import com.huster.bbs.model.LoginTicket;
import com.huster.bbs.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;

    //查询用户方法
    public User getUser(int id) {
        return userDAO.getUser(id);
    }

    //根据用户名查询用户
    public User getUserByName(String name) {
        return userDAO.selectByUsername(name);
    }

    //注册用户方法
    public Map<String, String> register(String username, String password) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isEmpty(username)) {
            map.put("msg","用户名不能为空");
            return map;
        }
        if (StringUtils.isEmpty(password)) {
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByUsername(username);//查询数据库，判断用户名是否已经被用过。
        if (user != null) {
            map.put("msg","用户名已经被注册");
            return map;
        }
        user = new User();//
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(BBSUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);//返回自增字段的userId值

        //注册通过，则将生成的ticket返回，由controller设置到cookie中
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    //用户登录校验方法
    public Map<String, String> login(String username, String password) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isEmpty(username)) {
            map.put("msg","用户名不能为空");
            return map;
        }
        if (StringUtils.isEmpty(password)) {
            map.put("msg","密码不能为空");
            return map;
        }
        //1.查询数据库，判断用户名是否存在
        User user = userDAO.selectByUsername(username);
        if (user == null) {
            map.put("msg","用户名不存在");
            return map;
        }
        //2.判断用户密码与数据库里存储的是否一致
        if (!BBSUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msg","密码错误");
            return map;
        }
        //校验通过，则将生成的ticket返回，由controller设置到cookie中
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    //增加一个userId的ticket，并实现插入数据库
    public String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+ 3600*24*1000);//设置数据库中存储的过期时间为一天
        ticket.setExpired(date);
        ticket.setStatus(0);//0表示没有登出
        String t = UUID.randomUUID().toString().replaceAll("-","");//生成的ticket
        ticket.setTicket(t);
        loginTicketDAO.addTicket(ticket);
        return t;
    }

    //用户登出时使用的服务
    public void logout(String ticket) {
        loginTicketDAO.updateStatusByTicket(ticket, 1);
    }
}
