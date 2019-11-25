package com.huster.bbs.controller;
import com.huster.bbs.model.*;
import com.huster.bbs.service.CommentService;
import com.huster.bbs.service.FollowService;
import com.huster.bbs.service.QuestionService;
import com.huster.bbs.service.UserService;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 新的首页
 */
@Controller
public class IndexController {

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHodler hostHodler;
    @Autowired
    JedisAdapter jedisAdapter;

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);


    @RequestMapping(path = {"/index","/"}, method ={RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, HttpServletRequest request) {
        if (request.getParameter("more") != null) {
            model.addAttribute("vos", getAllQuestions(0));
            return "index";
        }
        model.addAttribute("vos", getQuestions(0,0,10));
        return "index";
    }

    @RequestMapping(path = {"/user/{id}"}, method = {RequestMethod.GET,RequestMethod.POST})
    public String index(Model model, @PathVariable("id") int userId, HttpServletRequest request) {
        if (request.getParameter("more") != null) {
            model.addAttribute("vos", getQuestionsByIdsAndUser(userId, 0, Integer.MAX_VALUE));

        } else {
            model.addAttribute("vos", getQuestionsByIdsAndUser(userId,0,10));
        }
        // 显示关注和被关注列表
        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        if (hostHodler.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHodler.getUser().getId(), userId, EntityType.ENTITY_USER));
        } else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

    //私有方法，用于根据条件查询问题，并将问题包装成ViewObject
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Integer> ids = getTargetQuestionIds(offset, limit);
        List<ViewObject> vos = new ArrayList<ViewObject>(Arrays.asList(new ViewObject[ids.size()]));
        List<Question> questions = questionService.getQuestionsByIds(ids);
        for (Question question : questions) {//对于查询到的每个问题，都需要将问题信息+用户信息  包装进ViewObject，然后传递至前端页面
            ViewObject obj = new ViewObject();
            obj.set("question", question);
            User user = userService.getUser(question.getUserId());
            //问题关注的数量
            obj.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            obj.set("user", user);
            vos.set(ids.indexOf(question.getId()), obj);
        }
        return vos;
    }

    //得到所有问题，点击更多按钮实现
    private List<ViewObject> getAllQuestions(int userId) {
        List<Integer> ids = getTargetQuestionIds(0, -1);
        List<ViewObject> vos = new ArrayList<ViewObject>(Arrays.asList(new ViewObject[ids.size()]));
        List<Question> questions = questionService.getQuestionsByIds(ids);

        for (Question question : questions) {//对于查询到的每个问题，都需要将问题信息+用户信息  包装进ViewObject，然后传递至前端页面
            ViewObject obj = new ViewObject();
            obj.set("question", question);
            User user = userService.getUser(question.getUserId());
            //问题关注的数量
            obj.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            obj.set("user", user);
            vos.set(ids.indexOf(question.getId()), obj);
        }
        return vos;
    }

    //私有方法，用于根据条件查询问题，并将问题包装成ViewObject
    private List<ViewObject> getQuestionsByIdsAndUser(int userId, int offset, int limit) {
        List<Integer> ids = getTargetQuestionIds(offset, limit);
        List<Question> questions = questionService.getQuestionsByIdsAndUser(ids, userId);
        Collections.sort(questions, new Comparator<Question>() {//问题降序排列
            @Override
            public int compare(Question o1, Question o2) {
                return ids.indexOf(o1.getId()) - ids.indexOf(o2.getId());
            }
        });
        List<ViewObject> vos = new ArrayList<ViewObject>(Arrays.asList(new ViewObject[questions.size()]));
        for (Question question : questions) {//对于查询到的每个问题，都需要将问题信息+用户信息  包装进ViewObject，然后传递至前端页面
            ViewObject obj = new ViewObject();
            obj.set("question", question);
            User user = userService.getUser(question.getUserId());
            //问题关注的数量
            obj.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            obj.set("user", user);
            vos.set(questions.indexOf(question), obj);
        }
        return vos;
    }

    //得到问题积分表中的某个排名内的所有问题的id
    private List<Integer> getTargetQuestionIds(int offset, int limit) {
        Set<String> questionIds = jedisAdapter.zrevrange(RedisKeyUtil.getQuestionScoreKey(), offset, limit);
        List<Integer> ids = new ArrayList<>();
        for (String id : questionIds) {
            int i = Integer.parseInt(id);
            ids.add(i);
        }
        return ids;
    }
}
