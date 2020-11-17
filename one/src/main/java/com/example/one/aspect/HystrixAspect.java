package com.example.one.aspect;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Aspect
//@Component
public class HystrixAspect {

	
	//方法无意义，只提供一个名字
//    @Pointcut("execution(public * com.example.one.controller.*.*(..))")
    @Pointcut("execution(public * com.example.one.controller.TestHystrixController.*(..))")
    public void hystrixAspect(){}

    @Before("hystrixAspect()")
    public void doBefore(JoinPoint joinPoint){
    	Object th = joinPoint.getThis();
    	Object tar = joinPoint.getTarget();
//    	com.example.one.controller.HelloController cont=(com.example.one.controller.HelloController)tar;
        System.out.println("hystrixAspect--doBefore");
    }

    @After("hystrixAspect()")
    public void doAfter(JoinPoint joinPoint){
        System.out.println("hystrixAspect--doAfter");
    }

    @AfterReturning("hystrixAspect()")
    public void doAfterReturning(JoinPoint joinPoint){
        System.out.println("hystrixAspect--doAfterReturning");
    }

    @AfterThrowing("hystrixAspect()")
    public void deAfterThrowing(JoinPoint joinPoint){
        System.out.println("hystrixAspect--deAfterThrowing");
    }

    @Around("hystrixAspect()")
    public Object deAround(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("deAround 方法开始时间是:"+new Date());//在doBefore执行前执行。
        
        Object res = joinPoint.proceed();
        
        System.out.println("deAround 方法结束时间是:"+new Date());//在doAfter执行前执行
        return res;
    }
}
