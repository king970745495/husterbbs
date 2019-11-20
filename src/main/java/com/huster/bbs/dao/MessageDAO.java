package com.huster.bbs.dao;

import com.huster.bbs.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, has_read, conversation_id, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    //插入消息
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    //根据会话id查询消息，分页显示
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset, @Param("limit") int limit);

    //查询会话中未读的消息
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConvesationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    //查询某一个用户与其它用户进行交互的所有信息，显示是最新的一条
   /* @Select({"select ", INSERT_FIELDS, " ,count(id) as id from ( select * from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by id desc) tt group by tt.conversation_id  order by tt.created_date desc limit #{offset}, #{limit}"})*/
   @Select({"SELECT a.id ,a.from_id, a.to_id, a.content, a.has_read, a.conversation_id, a.created_date FROM ",TABLE_NAME," a\n" +
           "where (SELECT COUNT(*) FROM\n" +
           " (\n" +
           "        SELECT MAX(created_date) 'created_date'\n" +
           "        FROM\n" +
           "            message\n" +
           "where from_id=#{userId} or to_id=#{userId}"+
           "        GROUP BY\n" +
           "            conversation_id\n" +
           "    ) b\n" +
           "WHERE b.created_date = a.created_date != 0)\n" +
           "ORDER BY\n" +
           "    a.created_date DESC"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset, @Param("limit") int limit);

    //查询某个用户未读消息的条数
    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    //修改消息是否已读
    @Update({"update ", TABLE_NAME, " set has_read=1 where conversation_id=#{conversationId} and has_read = 0 and to_id = #{toId}"})
    int updateConversationStatus(@Param("conversationId") String conversationId, @Param("toId") int toId);
}
