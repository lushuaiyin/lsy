package com.example.one.aspect;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.*;



/*
@Aspect 表明是一个切面类
@Component 将当前类注入到Spring容器内
@Pointcut 切入点，其中execution用于使用切面的连接点。使用方法：execution(方法修饰符(可选) 返回类型 方法名 参数 异常模式(可选)) ，
可以使用通配符匹配字符，*可以匹配任意字符。
@Before 在方法前执行
@After 在方法后执行
@AfterReturning 在方法执行后返回一个结果后执行
@AfterThrowing 在方法执行过程中抛出异常的时候执行
@Around 环绕通知，就是可以在执行前后都使用，这个方法参数必须为ProceedingJoinPoint，proceed()方法就是被切面的方法，
上面四个方法可以使用JoinPoint，JoinPoint包含了类名，被切面的方法名，参数等信息。



execution(* com.sample.service.impl..*.*(..))
符号	含义
execution（）	表达式的主体；
第一个*符号	表示返回值的类型任意；
com.sample.service.impl	AOP所切的服务的包名，即，我们的业务部分
包名后面的..	表示当前包及子包
第二个*	表示类名，*即所有类。此处可以自定义，下文有举例
.*(..)	表示任何方法名，括号表示参数，两个点表示任何参数类型

例如 @Pointcut("execution(public * com.example.one.controller.HelloController.hello*(..))")

 */
@Aspect
@Component
public class LogAspect {
//    @Pointcut("execution(public * com.example.one.controller.*.*(..))")
//    public void LogAspect(){}
//
//    @Before("LogAspect()")
//    public void doBefore(JoinPoint joinPoint){
//        System.out.println("doBefore");
//    }
//
//    @After("LogAspect()")
//    public void doAfter(JoinPoint joinPoint){
//        System.out.println("doAfter");
//    }
//
//    @AfterReturning("LogAspect()")
//    public void doAfterReturning(JoinPoint joinPoint){
//        System.out.println("doAfterReturning");
//    }
//
//    @AfterThrowing("LogAspect()")
//    public void deAfterThrowing(JoinPoint joinPoint){
//        System.out.println("deAfterThrowing");
//    }
//
//    @Around("LogAspect()")
//    public Object deAround(ProceedingJoinPoint joinPoint) throws Throwable{
//        System.out.println("deAround");
//        return joinPoint.proceed();
//    }

	
	
	/*
	 打印：
	 
	 
deAround 方法开始时间是:Thu Sep 03 10:40:32 CST 2020
doBefore
hello !  I am   <span style="color:red">[one:172.20.10.2:8901]</span>...2020-09-03 10:40:35
deAround 方法结束时间是:Thu Sep 03 10:40:35 CST 2020
doAfter
doAfterReturning

	 
	 
	 */
	
	//方法无意义，只提供一个名字
//    @Pointcut("execution(public * com.example.one.controller.*.*(..))")
    @Pointcut("execution(public * com.example.one.controller.HelloController.hello*(..))")
    public void oneAspect(){}

    @Before("oneAspect()")
    public void doBefore(JoinPoint joinPoint){
    	Object th = joinPoint.getThis();
    	Object tar = joinPoint.getTarget();
//    	com.example.one.controller.HelloController cont=(com.example.one.controller.HelloController)tar;
        System.out.println("doBefore");
    }

    @After("oneAspect()")
    public void doAfter(JoinPoint joinPoint){
        System.out.println("doAfter");
    }

    @AfterReturning("oneAspect()")
    public void doAfterReturning(JoinPoint joinPoint){
        System.out.println("doAfterReturning");
    }

    @AfterThrowing("oneAspect()")
    public void deAfterThrowing(JoinPoint joinPoint){
        System.out.println("deAfterThrowing");
    }

    @Around("oneAspect()")
    public Object deAround(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("deAround 方法开始时间是:"+new Date());//在doBefore执行前执行。
        
        Object res = joinPoint.proceed();
        
        System.out.println("deAround 方法结束时间是:"+new Date());//在doAfter执行前执行
        return res;
    }
}
