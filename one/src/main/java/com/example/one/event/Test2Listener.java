package com.example.one.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author lsy
 *
 */
@Component
public class Test2Listener {

	@EventListener
	public void onApplicationEvent(TestEvent event) {
		// TODO Auto-generated method stub
		if(event.getSource()!=null) {
			MessageBean bean=(MessageBean)event.getSource();
			System.out.println("22收到消息==="+bean.toString());
		}
	}
}
