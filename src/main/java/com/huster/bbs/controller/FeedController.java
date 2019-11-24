package com.huster.bbs.controller;

import com.huster.bbs.model.EntityType;
import com.huster.bbs.model.Feed;
import com.huster.bbs.model.HostHodler;
import com.huster.bbs.service.FeedService;
import com.huster.bbs.service.FollowService;
import com.huster.bbs.utils.JedisAdapter;
import com.huster.bbs.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    @Autowired
    FeedService feedService;

    @Autowired
    HostHodler hostHodler;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;


    //拉取模式，将某个用户所有关注的用户，所有的新鲜事拉取过来
    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET})
    public String getPullFeeds(Model model) {
        int localUserId = hostHodler.getUser() == null ? 0 : hostHodler.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0) {
            // 关注的人
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    //推模式，当某个用户产生了新的事件以后，会将这个新的事件的id推送到他的粉丝的timeline的list中
    @GetMapping(value = "/pushfeeds")
    public String getPushFeeds(Model model) {
        int localUserId = hostHodler.getUser() == null ? 0 : hostHodler.getUser().getId();
        //得到redis中某个用户的时间轴，RedisKeyUtil.getTimelineKey(localUserId)得到当前登录用户的redis中的键
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);//取最新的十条事件
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed != null) {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

}
