package com.huster.bbs.repository;

import com.huster.bbs.model.Question;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface QuestionRepository extends ElasticsearchRepository<Question,Long>{

    List<Question> findByTitle(String title);

}