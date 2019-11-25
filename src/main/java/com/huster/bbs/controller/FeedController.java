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

        //【推拉结合】
        List<Feed> feeds = new ArrayList<>();//最终的新鲜事的列表

        //得到redis中某个用户的时间轴，RedisKeyUtil.getTimelineKey(localUserId)得到当前登录用户的redis中的键
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);//取推送模式中，最新的十条事件
        List<Integer> followees = new ArrayList<>();;//推策略得到的问题不够时，才结合拉策略，这里先简单的不是推就是拉

        if (feedIds.size() < 10) {//推的新鲜事不够时，使用拉取的方式，拉取最新的10人的新鲜事
            if (localUserId != 0) {
                // 关注的人
                followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
            }
            feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
            /*for (int followee : followees) {//下面是结合的过程
            }*/
        } else {//推送的新鲜事已经满足十条的要求，取其中的十条进行展示
            for (String feedId : feedIds) {
                Feed feed = feedService.getById(Integer.parseInt(feedId));
                if (feed != null) {
                    feeds.add(feed);
                }
                if (feeds.size() >= 10) break;;
            }
        }
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
