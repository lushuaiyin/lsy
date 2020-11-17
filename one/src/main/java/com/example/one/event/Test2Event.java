package com.example.one.event;

import org.springframework.context.ApplicationEvent;

/**
 * 
 * 事件本身的触发需要我们调用ApplicationContext的publishEvent方法，这个需要在具体业务场景由我们决定执行触发。
 * 这样监听类就可以监听到事件，执行具体的逻辑
 * 
 * @author lsy
 *
 */
public class Test2Event extends ApplicationEvent {

	private MessageBean bean;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Test2Event(Object source,MessageBean bean) {
		super(source);
		// TODO Auto-generated constructor stub
		this.bean=bean;
	}

	public MessageBean getBean() {
		return bean;
	}


}
