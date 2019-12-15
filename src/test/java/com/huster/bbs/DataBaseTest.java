package com.huster.bbs;
import com.huster.bbs.dao.UserDAO;
import com.huster.bbs.dao.QuestionDAO;
import com.huster.bbs.model.EntityType;
import com.huster.bbs.model.Question;
import com.huster.bbs.model.User;
import com.huster.bbs.service.FollowService;
import com.huster.bbs.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/init-schema.sql")//让测试程序在执行前，执行这个sql文件里的语句
class DataBaseTest {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;

    //初始化表中的数据，插入一些包含与非包含关系
    @Test
    public void initDatabase() {
        Random random = new Random();

        for(int i=0; i<11; ++i){
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d",i+1));
            user.setPassword("123");
            user.setSalt("");
            userDAO.addUser(user);

            //互相关注的测试数据的生成
            for (int j = 0; j< i; ++j){
                followService.follow(j+1, EntityType.ENTITY_USER, i+1);
            }

            Question question = new Question();
//            question.setCommentCount(i+1);
            question.setCommentCount(0);
            Date date = new Date();
            date.setTime(date.getTime() + 60*60*1000*i);
            question.setCreatedDate(date);
            question.setUserId(i+1);
            question.setTitle(String.format("Title%d",i+1));
            question.setContent(String.format("Content %d",i+1));
            questionService.addQuestion(question);
        }
    }
}
