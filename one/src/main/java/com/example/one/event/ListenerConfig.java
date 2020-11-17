package com.example.one.event;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 使用配置类的写法
 * @author lsy
 *
 */
@Configuration
public class ListenerConfig {

	@EventListener
	public void handleTest222Event(TestEvent event) {
		// TODO Auto-generated method stub
		if(event.getSource()!=null) {
			MessageBean bean=(MessageBean)event.getSource();
			System.out.println("222###收到消息==="+bean.toString());
		}
	}
	
	@EventListener
	public void handleTest2222Event(Test2Event event) {
		// TODO Auto-generated method stub
		if(event.getSource()!=null) {
			MessageBean bean=event.getBean();
			System.out.println("2222###收到消息==="+bean.toString());
		}
	}
}
