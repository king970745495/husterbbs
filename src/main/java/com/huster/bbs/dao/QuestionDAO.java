package com.huster.bbs.dao;

import com.huster.bbs.model.Question;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface QuestionDAO {

    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title,content,user_id,created_date,comment_count ";
    String SELECT_FIELDS = " id,"+INSERT_FIELDS;

    @Insert({"INSERT INTO "+TABLE_NAME+" ("+INSERT_FIELDS+") VALUES (#{title},#{content},#{userId},#{createdDate},#{commentCount})"})
    int addQuestion(Question question);

    @Select({"SELECT "+SELECT_FIELDS+" from "+TABLE_NAME+" WHERE id = #{id}"})
    List<Question> getQuestions(int id);

    //#{offset},#{limit}userId
    List<Question> selectLatestQuestion(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

}
