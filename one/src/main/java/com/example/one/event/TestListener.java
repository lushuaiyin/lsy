package com.example.one.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 指定泛型的时间类型是我们定义的TestEvent
 * 监听3种方式：
 * 1，实现ApplicationListener接口
 * 2，使用注解@EventListener
 * 3,启动main方法时注册
 * //注册监听的第3种方式,spring自带的监听只能用这种方式。
		SpringApplication app =new SpringApplication(OneApplication.class);
        app.addListeners(new TestListener());
        app.run(args);
 * 触发一个事件，可以被多个监听监听到。
 * 出发的事件与监听的对应关系是监听类的泛型决定的，
 * 例如，触发applicationContext.publishEvent(new TestEvent(mess));
 * ApplicationListener<TestEvent>会监听到，没有泛型的监听类不会监听到
 * 
 * 另，一个监听如果被注册多次就会被执行多次。
 * 
 * 
 * 关于监听的用途，有段文摘：
 * 
 * spring的事件应该是在3.x版本就发布的功能了，并越来越完善，其为bean和bean之间的消息通信提供了支持。
 * 比如，我们可以在用户注册成功后，发送一份注册成功的邮件至用户邮箱或者发送短信。
 * 使用事件其实最大作用，应该还是为了业务解耦，毕竟用户注册成功后，注册服务的事情就做完了，
 * 只需要发布一个用户注册成功的事件，让其他监听了此事件的业务系统去做剩下的事件就好了。
 * 对于事件发布者而言，不需要关心谁监听了该事件，以此来解耦业务。今天，我们就来讲讲spring boot中事件的使用和发布。
 * 当然了，也可以使用像guava的eventbus或者异步框架Reactor来处理此类业务需求的。
 * 本文仅仅谈论ApplicationEvent以及Listener的使用。
 * 
 * 
 * @author lsy
 *
 */
@Component
public class TestListener implements ApplicationListener<TestEvent>{

	@Override
	public void onApplicationEvent(TestEvent event) {
		// TODO Auto-generated method stub
		if(event.getSource()!=null) {
			MessageBean bean=(MessageBean)event.getSource();
			System.out.println("11收到消息==="+bean.toString());
		}
	}

}
