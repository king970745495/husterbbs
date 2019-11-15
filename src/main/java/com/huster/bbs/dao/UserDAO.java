package com.huster.bbs.dao;

import com.huster.bbs.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {

    String TABLE_NAME = " user ";//数据库表名
    String INSERT_FIELDS = " NAME,PASSWORD,salt,head_url ";//插入域

    String SELECT_FIELDS = " id,"+INSERT_FIELDS;//查询域

    @Insert({"INSERT INTO "+TABLE_NAME+" ("+INSERT_FIELDS+") VALUES (#{name},#{password},#{salt},#{headUrl})"})
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void addUser(User user);//将一个用户插入数据库

    @Select({"SELECT "+SELECT_FIELDS+" from "+TABLE_NAME+" WHERE id = #{id}"})
    User getUser(int id);//根据用户id查询用户

    @Select({"SELECT "+SELECT_FIELDS+" from "+TABLE_NAME+" WHERE name = #{username}"})
    User selectByUsername(String username);//根据用户名查询用户

    @Update({"UPDATE ", TABLE_NAME, " SET `name` = #{name},`password`=#{password} WHERE id = #{id}"})
    void updateUser(User user);//更新用户

    @Delete({"DELETE from ",TABLE_NAME," WHERE id = #{id}"})
    void deleteUser (int id);//删除用户

}
