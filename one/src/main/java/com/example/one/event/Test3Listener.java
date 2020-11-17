package com.example.one.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class Test3Listener implements ApplicationListener{

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		if(event.getSource()!=null) {
			Object bean= event.getSource();
			System.out.println("33收到消息==="+bean.toString());
		}
	}


}
