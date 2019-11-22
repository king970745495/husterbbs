package com.huster.bbs.service;

import com.huster.bbs.controller.IndexController;
import com.huster.bbs.dao.QuestionDAO;
import com.huster.bbs.model.Question;
import com.huster.bbs.utils.BBSUtil;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    JedisAdapter jedisAdapter;

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
        try {
            questionDAO.addQuestion(question);

            //更新redis的数据
            //存入redis的zset中
            jedisAdapter.zadd(RedisKeyUtil.getQuestionCommentCountKey(),0, String.valueOf(question.getId()));
            //存入问题的分数zset中
            jedisAdapter.zadd(RedisKeyUtil.getQuestionScoreKey(), BBSUtil.getQuestionScores(question.getId()
                    , jedisAdapter.zscore(RedisKeyUtil.getQuestionCommentCountKey(), String.valueOf(question.getId()))), String.valueOf(question.getId()));
            return question.getId();
        } catch (Exception e) {
            logger.error("问题插入失败！");
            return 0;
        }

        //在增加之前还可以做敏感词的过滤
//        return  > 0 ? question.getId() : 0;
    }

    public Question getQuestionById(int id) {
        return questionDAO.getQuestionById(id);
    }

    //查询所有问题
    public List<Question> getQuestions(int id) {
        return questionDAO.getQuestions(id);
    }
    //根据id的List查询所有的问题
    public List<Question> getQuestionsByIds(List<Integer> ids) {
        if (ids.size() > 0) {
            String idStr = "'"+ StringUtils.join(ids,"','")+"'";//将List转换为字符串
            return questionDAO.getQuestionsByIds(idStr);
        } else {
            return new ArrayList<Question>();
        }
    }

    //更新评论的数量
    public int updateCommentCount(int id, int comment_count) {
        return questionDAO.updateCommentCount(id, comment_count);
    }

}
