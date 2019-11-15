package com.huster.bbs.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Component
@Aspect
public class LogAspect {
    private final static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcode.springtest.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) sb.append("arg:" + arg.toString() + "|");
        }
        logger.info("before Method" + sb.toString());
    }
    @After("execution(* com.nowcode.springtest.controller.*Controller.*(..))")
    public void afterMethod() {
        logger.info("after Method");
    }
}
