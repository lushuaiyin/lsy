package com.example.one.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.one.event.MessageBean;
import com.example.one.event.Test2Event;
import com.example.one.event.TestEvent;
import com.example.one.tmp.LogUtil;

//@Component
@RestController
//@RequestMapping("/beat")
public class HelloController {
	public static Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	
//	@Autowired
//	private ComConfig conf;

	private String myvalue="000";
	
	
	@GetMapping(value="/log")
	public String log() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		String message="hi !  I am   <span style=\"color:"+colorArr[0]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+timeNow;
		System.out.println(message);
		
		int i=0;
		while(i<5000*5) {
			i++;
			String infff="测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试"
					+ "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试"
					+ "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试";
			logger.info(i+"=="+infff);
		}
		System.out.println("==============");
		
		return message;
	}
	
	@GetMapping(value="/chvalue")
    public String chvalue(String v){
        System.out.println("myvalue=="+myvalue+",,,,v==="+v); 
        myvalue=v;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return myvalue;
    }
	
	@GetMapping(value="/hello")
	public String hello() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		String message="hello !  I am   <span style=\"color:"+colorArr[0]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+timeNow;
		System.out.println(message);
		return message;
	}
	
	@GetMapping(value="/hi")
	public String hi() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		String message="hi !  I am   <span style=\"color:"+colorArr[0]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+timeNow;
		System.out.println(message);
		return message;
	}
	
	@PostMapping(value="/hello2")
	public String hello2() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		String message="hello2 POST请求 !  I am   <span style=\"color:"+colorArr[0]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+timeNow;
		System.out.println(message);
		return message;
	}
	/**
	 * ResponseBody注解表示该方法的返回的结果直接写入 HTTP 响应正文（ResponseBody）中
	 * 使用时机：返回的数据不是html标签的页面，而是其他某种格式的数据时（如json、xml等）使用
	 * 
	 * @param jsonReq
	 * @return
	 */
	@RequestMapping(value = "/hello3",method = RequestMethod.POST)
    @ResponseBody
    public Map hello3(@RequestBody String jsonReq){
        System.out.println("hello3接收请求体为===\r\n"+jsonReq); 
        Map hm=new HashMap();
        hm.put("uname", "lsy");
        hm.put("upass", "123456啦啦啦");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hm.put("time", LocalDateTime.now().format(df));
        return hm;
    }
	
	@RequestMapping(value = "/hello4",method = RequestMethod.POST)
    @ResponseBody
    public String hello4(@RequestBody String jsonReq){
		System.out.println("hello4接收请求体为===\r\n"+jsonReq); 
        Map hm=new HashMap();
        hm.put("uname", "lsy");
        hm.put("upass", "45678啦啦啦");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hm.put("time", LocalDateTime.now().format(df));
		System.out.println("test response==="+hm.toString());
        return "test response==="+hm.toString();
    }
	
	
	@RequestMapping(value = "/testhyxx",method = RequestMethod.GET)
	@ResponseBody
	public String testhyxx() {
		try {
			// 直接正常返回，用于填充熔断采样请求的百分比
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String info="【testhy0】0000000 直接返回===  hello !  I am   [" + servername + ":" + ip + ":" + port + "]" + "..."
					+ LocalDateTime.now().format(df);
			System.out.println(info);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return "异常testhy0";
		}
	}
	@Autowired
	private ApplicationContext applicationContext;
	
	@GetMapping(value="/eventTest")
	public String eventTest() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		
		MessageBean mess=new MessageBean();
		mess.setMess("hello!");
		mess.setType("1");
		System.out.println("触发事件。。。start");
		applicationContext.publishEvent(new TestEvent(mess));
		System.out.println("触发事件。。。end");
		
		System.out.println("触发事件22。。。start");
		applicationContext.publishEvent(new Test2Event(this,mess));//讲事件发生的对象this传进去。消息用另一个bean。
		System.out.println("触发事件22。。。end");
		
		String message="hello !  I am   ["+servername+":"+ip+":"+port+"]"+"..."+timeNow;
		System.out.println(message);
		return message;
	}
	
}
