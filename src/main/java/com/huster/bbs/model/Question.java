package com.huster.bbs.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

//@Document(indexName = "bbs", type = "question")
@Document(indexName = "bbs", type = "question")
public class Question {

    @Id
    @Field(store = true, index = false, type = FieldType.Integer)
    private int id;
    @Field(store = true, index = true, type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String title;
    @Field(store = true, index = true, type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
    private String content;
    //@Field(store = true, index = true, type = FieldType.Integer, analyzer = "bbsanalyzer", searchAnalyzer = "bbsanalyzer")
    private int userId;
    //@Field(store = true, index = true, type = FieldType.Text, analyzer = "bbsanalyzer", searchAnalyzer = "bbsanalyzer")
    private Date createdDate;
    //@Field(store = true, index = true, type = FieldType.Integer, analyzer = "bbsanalyzer", searchAnalyzer = "bbsanalyzer")
    private int commentCount;

    public Question() {
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", createdDate=" + createdDate +
                ", commentCount=" + commentCount +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
