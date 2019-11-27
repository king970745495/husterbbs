package com.huster.bbs.service;

import com.huster.bbs.model.Question;
import com.huster.bbs.repository.QuestionRepository;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
    @Autowired
    private QuestionRepository questionRepository;

    public void save(List<Question> questions) {
        questionRepository.saveAll(questions);

    }
    public void save(Question question) {
        questionRepository.save(question);
    }

    public List<Question> testSearch(String keywords, int offset, int limit) {
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(keywords);
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Question> page = questionRepository.search(builder, pageable);
        return page.getContent();
    }
    public List<Question> searchByTitle(String title) {

        return questionRepository.findByTitle(title);
    }
}