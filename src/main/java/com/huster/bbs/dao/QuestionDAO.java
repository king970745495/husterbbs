package com.huster.bbs.dao;

import com.huster.bbs.model.Question;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface QuestionDAO {

    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title,content,user_id,created_date,comment_count ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    //插入问题
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert({"INSERT INTO "+TABLE_NAME+" ("+INSERT_FIELDS+") VALUES (#{title},#{content},#{userId},#{createdDate},#{commentCount})"})
    int addQuestion(Question question);

    //查询所有问题
    @Select({"SELECT "+SELECT_FIELDS+" from "+TABLE_NAME})//+" WHERE id = #{id}"
    List<Question> getQuestions(int id);
    //查询最新的问题
    //#{offset},#{limit}userId
    List<Question> selectLatestQuestion(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);
    //根据id查询问题
    @Select({"SELECT "+SELECT_FIELDS+" from "+TABLE_NAME+" WHERE id = #{id}"})
    Question getQuestionById(int id);

    @Update({"update ", TABLE_NAME, "set comment_count = #{comment_count} where id  = #{id}"})
    int updateCommentCount(int id, int comment_count);

    //根据id查询所有问题，使用xml文件进行映射
    @Select({"SELECT "+SELECT_FIELDS+" from "+TABLE_NAME,"WHERE id in (${ids})"})//+" WHERE id = #{id}"
    List<Question> getQuestionsByIds(String ids);

}
