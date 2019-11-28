# husterbbs
HusterBBS项目，该项目以springboot新型的web技术，实现了一个原来存在的BBS论坛的问答模式的系统，实现了一个校园、班级、或者其它的小团体间信息、话题实时交互的网站，以应对校园中的此类话题交互的需求。

## 1.注册登录模块【使用数据库模拟session，存储一个用户的唯一token】
![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs1.1.png)
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs1.2.png)
### 如果未注册，会显示用户名不存在。
### 注册登录完成后，会使用一个ThreadLocal<User>的变量记录当前线程的登录用户的信息
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs1.3.png)

## 2.问答模块【提问（敏感词过滤）、评论、点赞、热点问题展示（排序规则），个人主页展示】
### 敏感词过滤，使用前缀树进行过滤
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs2.1.png)
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs2.2.png)
### 评论点赞，评论按照【点赞数——发布时间】排序
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs2.3.png)
 
### 首页热点问题展示：
#### 问题排序的分数，使用公式 Score = (P-1)/(T+2)^G 计算得出：与评论数、问题关注人数P、问题发布时间T相关，G代表重力系数
![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs2.4.png)
 
### 个人主页展示：展示当前登录用户的所有提出过的问题详情
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs2.5.png)

## 3.消息模块【互相发送消息（如果表示会话、与会话是否已读），系统提示消息（点赞、评论会给相应的用户发送系统消息，异步框架发送消息）】
 ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs3.1.png)
### 会建立一个两个用户间的会话，并且被发送消息方会有未读消息的提示
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs3.2.png)
	  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs3.3.png)
### 其它用户的关注、对某用户发布问题的关注、点赞等行为，会触发异步框架，由系统的身份给该用户发送系统提示消息
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs3.4.png)

## 4.特别关心模块【关注用户、问题（redis），发布了新的问题/有了新的评论的时候，在特别关心这里展示动态，推送与拉取结合】     
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs4.1.png)
   ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs4.2.png)
 
### 用户可以对其它用户，或者问题点击关注，然后用户就可以在关注的Tab页查看到关注的对象的动态
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs4.3.png)
 
采用redis对每个用户的关注的动态信息进行存储，并且采取动态信息【推送+拉取结合的方式】：若某用户的粉丝数>100，则他的动态不会广播发送给他的粉丝进行存储；否则，每次有动态的时候，会将该用户新的动态发送给粉丝的redis中的时间轴list，进行存储。在点击关注信息时，再将关注的用户中粉丝数>100的用户的动态拉取过来，进行整合显示。

## 5.爬虫模块
### 采用pyspider爬取其它网页中的高赞问题至数据库中。
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs5.1.png)

## 6.搜索模块
### 采用elasticsearch与springboot-data进行整合，使用中文分词器进行分词。实现新增问题时，将该问题加入elasticsearch索引库中建立索引；查询问题时对查询字符串进行分词后，在索引库中搜索索引，得到搜索结果后，返回前端页面。
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs6.1.png)
新建索引hustbbs

### 搜索结果页面
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs6.2.png)


## 7.nginx实现虚拟域名：
### 使用nginx实现虚拟主机，虚拟域名为www.husterbbs.com，nginx监听特定机器上的80端口，监听到配置的端口后实现反向代理，转发请求到上游服务器。
  ![images](https://github.com/king970745495/husterbbs/blob/master/images-folder/hustbbs7.1.png)

