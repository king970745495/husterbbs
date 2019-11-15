package com.huster.bbs.service;

import com.huster.bbs.dao.QuestionDAO;
import com.huster.bbs.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    public List<Question> getLatestQuestion(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestion(userId, offset, limit);
    }

}
