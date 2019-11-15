package com.huster.bbs.service;

import com.huster.bbs.dao.LoginTicketDAO;
import com.huster.bbs.model.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginTicketService {

    @Autowired
    LoginTicketDAO loginTicketDAO;
    //增加ticket
    public void addLoginTicket(LoginTicket ticket) {
        loginTicketDAO.addTicket(ticket);
    }
    //查询ticket条目
    public LoginTicket selectTicket(String ticket) {
        return loginTicketDAO.selectByTicket(ticket);
    }





}
