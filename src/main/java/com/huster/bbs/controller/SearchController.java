package com.huster.bbs.controller;

import com.huster.bbs.model.EntityType;
import com.huster.bbs.model.Question;
import com.huster.bbs.model.ViewObject;
import com.huster.bbs.service.FollowService;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.SearchService;
import com.huster.bbs.service.UserService;
import com.huster.bbs.utils.BBSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {
    @Autowired
    SearchService searchService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    // 保存数据库所有question到es
    @GetMapping(value = "/save")
    @ResponseBody
    public String save() {
        List<Question> questions = questionService.getLatestQuestion(0, 0, 500); // 500是按数据量写的
        searchService.save(questions);
        return BBSUtil.getJsonString(0);
    }

    @GetMapping(value = "/search")
    public String search(Model model, @RequestParam("q") String keyword, @RequestParam(value = "p", defaultValue = "1") int p) {
        try {
            /*List<Question> questionList = searchService.searchByTitle("武汉");
            System.out.println(questionList.get(0).toString());
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                ViewObject vo = new ViewObject();
                vo.set("question", question);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userService.getUser(question.getUserId()));
                vos.add(vo);
            }*/

            int offset = (p - 1) * 10;
            List<Question> questionList = searchService.testSearch(keyword, offset, 10);
            System.out.println("搜到" + questionList.size() + "个问题");
            List<ViewObject> vos = new ArrayList<>();
            for (Question question : questionList) {
                ViewObject vo = new ViewObject();
                vo.set("question", question);
                vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
                vo.set("user", userService.getUser(question.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("vos", vos);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {
            logger.error("搜索失败" + e.getMessage());
        }
        return "result";
    }
}