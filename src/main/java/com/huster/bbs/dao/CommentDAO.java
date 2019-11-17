package com.huster.bbs.dao;

import com.huster.bbs.model.Comment;
import com.huster.bbs.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentDAO {

    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id,content,created_date,entity_id, entity_type, status ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    //插入评论
    @Insert({"INSERT INTO "+TABLE_NAME+" ("+INSERT_FIELDS+") VALUES (#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    //根据实体，查询所有评论
    @Select({"SELECT ", SELECT_FIELDS, " from ", TABLE_NAME, " WHERE entity_id = #{entityId} and entity_type = #{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    //根据实体，查询评论数量
    @Select({"SELECT count(id) from", TABLE_NAME, " WHERE entity_id = #{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);


}
