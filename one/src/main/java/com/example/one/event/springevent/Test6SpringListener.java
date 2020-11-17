package com.example.one.event.springevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * spring自身的监听器有：
 * 
ApplicationStartingEvent
ApplicationFailedEvent
ApplicationPreparedEvent
ApplicationReadyEvent
ApplicationEnvironmentPreparedEvent
 * 
 * @author lsy
 *
 */
@Component
public class Test6SpringListener implements ApplicationListener<ApplicationFailedEvent>{

	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		// TODO Auto-generated method stub
		SpringApplication application = event.getSpringApplication();
        System.out.println("---------Test6SpringListener,启动失败----------");
	}

}
