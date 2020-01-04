package com.huster.bbs;

import com.huster.bbs.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/*
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
public class LikeServiceTests {

    @Autowired
    LikeService likeService;

    @BeforeClass
    public void beforeClass() {
        //在spring初始化的时候，如果多个单元测试的需要暂时创建的数据相同，就可以放在这里面
        System.out.println("beforeClass");
    }

    @AfterClass
    public void afterClass() {
        //清楚beforeClass创建的数据
        System.out.println("afterClass");
    }

    @Before
    public void setUp() {
        //如果在测试前有数据需要初始化，可以在这里初始化
        System.out.println("before");
    }

    @Test
    public void testLike() {
        System.out.println("testLike");
        likeService.like(123, 1, 1);
        Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));//断言测试的结果
    }

    @After
    public void tearDown() {
        //如果在测试后，需要清理before插入的数据，就在这里清理
        System.out.println("tearDown");
    }

    //期待得到异常就用这种测试
    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        System.out.println("testException");
    }

}*/
