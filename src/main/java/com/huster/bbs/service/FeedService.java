package com.huster.bbs.service;

import com.huster.bbs.dao.FeedDAO;
import com.huster.bbs.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    // '拉'模式
    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count) {
        return feedDAO.selectUserFeeds(maxId, userIds, count);
    }

    //增加feed
    public boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }

    // '推'模式，根据id查询feed
    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }
}