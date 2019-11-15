package com.huster.bbs.dao;

import com.huster.bbs.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDAO {

    String TABLE_NAME = " login_ticket ";
    String INSERT_FIELDS = " user_id, ticket, expired, status ";

    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"INSERT INTO "+TABLE_NAME+" ("+INSERT_FIELDS+") VALUES (#{userId},#{ticket},#{expired},#{status})"})
    int addTicket(LoginTicket ticket);

    @Select({"select", SELECT_FIELDS, "from", TABLE_NAME, "where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    //根据ticket修改ticket条目的状态（用于用户登出的时候，将用户的ticket信息删除）
    @Update({"update",TABLE_NAME, "set status = #{status} where ticket = #{ticket}" })//只有加了@param才可以被用作注解中的变量
    void updateStatusByTicket(@Param("ticket") String ticket, @Param("status") int status);

}
