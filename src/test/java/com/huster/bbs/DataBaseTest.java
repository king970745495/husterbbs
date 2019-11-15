package com.huster.bbs;
import com.huster.bbs.dao.UserDAO;
import com.huster.bbs.dao.QuestionDAO;
import com.huster.bbs.model.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
//@Sql("/init-schema.sql")//让测试程序在执行前，执行这个sql文件里的语句
class DataBaseTest {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuestionDAO questionDAO;


    @Test
    void contextLoads() {
        /**
         * question表部分
         */
        //插入
        /*Random random = new Random();
        for (int i = 0;i <11;i++) {
            Question question = new Question();
            question.setTitle("king-"+i);
            question.setCommentCount(10);
            question.setCreatedDate(new Date());
            question.setContent("zheshi...");
            question.setUserId(i);
            questionDAO.addQuestion(question);
        }*/
       /* List<Question> questions = questionDAO.getQuestions(1);
        for (Question question : questions) {
            System.out.println(question);
        }*/
        List<Question> questions = questionDAO.selectLatestQuestion(3, 1, 3);
        for (Question question : questions) {
            System.out.println(question);
        }

        /**
         * User表部分
         */
        //插入数据
        //Random random = new Random();
        /*for (int i = 0;i <11;i++) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
            user.setName(String.format("USER%d",i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);
        }*/

        //查询数据
        /*List<User> users = userDAO.getUsers(1);
        for (User user:users) {
            System.out.println(user);
        }*/

        //更新数据
        /*User user = new User();
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
        user.setName("kingliu");
        user.setPassword("123456789");
        user.setSalt("");
        user.setId(1);
        userDAO.updateUser(user);*/

        //删除数据
        /*userDAO.deleteUser(4);*/

    }
}
