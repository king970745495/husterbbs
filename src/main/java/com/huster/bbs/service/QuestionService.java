package com.huster.bbs.service;

import com.huster.bbs.dao.QuestionDAO;
import com.huster.bbs.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    SensitiveService sensitiveService;

    /**
     * 根据用户的id得到最新的一些问题
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Question> getLatestQuestion(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestion(userId, offset, limit);
    }

    /**
     * 增加问题的服务，成功返回问题id，失败返回0
     * @param question
     * @return
     */
    public int addQuestion(Question question) {
        //1.过滤html和JavaScript等内容，将其转义后进行存储
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));

        //2.敏感词过滤
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));

        //在增加之前还可以做敏感词的过滤
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public Question getQuestionById(int id) {
        return questionDAO.getQuestionById(id);
    }

}
