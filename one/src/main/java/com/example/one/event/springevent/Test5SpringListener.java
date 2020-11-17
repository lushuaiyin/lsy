package com.example.one.event.springevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * spring自身的监听器有：
 * 
1. An ApplicationStartingEvent is sent at the start of a run but before any processing, except for
47
the registration of listeners and initializers.

2. An ApplicationEnvironmentPreparedEvent is sent when the Environment to be used in the context is
known but before the context is created.
3. An ApplicationContextInitializedEvent is sent when the ApplicationContext is prepared and
ApplicationContextInitializers have been called but before any bean definitions are loaded.
4. An ApplicationPreparedEvent is sent just before the refresh is started but after bean definitions
have been loaded.
5. An ApplicationStartedEvent is sent after the context has been refreshed but before any
application and command-line runners have been called.
6. An AvailabilityChangeEvent is sent right after with LivenessState.CORRECT to indicate that the
application is considered as live.
7. An ApplicationReadyEvent is sent after any application and command-line runners have been
called.
8. An AvailabilityChangeEvent is sent right after with ReadinessState.ACCEPTING_TRAFFIC to indicate
that the application is ready to service requests.
9. An ApplicationFailedEvent is sent if there is an exception on startup.






 * 
 * @author lsy
 *
 */
@Component
public class Test5SpringListener implements ApplicationListener<ApplicationStartingEvent>{

	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {
		// TODO Auto-generated method stub
		SpringApplication application = event.getSpringApplication();
        System.out.println("---------Test5SpringListener,启动开始----------");
	}

}
