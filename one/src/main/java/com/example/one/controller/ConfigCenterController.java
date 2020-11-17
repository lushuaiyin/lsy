package com.example.one.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.one.bean.MyTestBean;


/**
 * 使用consul作为配置中心的测试
 * 
 * 一个方法用于查看这些属性的值，比较用@Value和 @Autowired加@ConfigurationProperties 两种方式的区别
 * 
 *  @RefreshScope
 * 有人做过实验，说consul作为配置中心，修改值后，@Value的对象值不变，还是之前的，@ConfigurationProperties对象的值就变了，是最新的。
 * 其实是没有加@RefreshScope注解导致的。
 * 
 * 
 * @author lsy
 *
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigCenterController {
	
	@Value("${mytest.myname}")
	private String myname;
	
	@Value("${mytest.myaddress}")
	private String myaddress;
	
	
	/**
	 * @Value("${mytest.mysalary:100万}")
	 * 冒号后跟的是默认值，要注意：不是在配置文件中这个属性为空才是用默认值，而是在属性文件中根本配有这个属性采用默认值。
	 * 如果你在属性文件中配置了这个属性，但是值没写东西，那么这个值也算是配置了，所以默认值不会起作用。
	 * 另外要注意，如果没有默认值，那么你在属性文件中必须配置这个属性，不然spring在做映射的时候找不到这个属性就报错了。
	 * 所以，一些对程序不重要的属性，可有可无，就配置上这个默认值。保证程序不会应为配置文件中少了这个属性导致应用无法启动。
	 * 
	 * @Value这个注解有这个限制，@ConfigurationProperties没有这个限制，@ConfigurationProperties找不到对应的属性值就是null，不会报错。
	 * 
	 */
	@Value("${mytest.mysalary:100万}")
//	@Value("${mytest.mysalary}")
	private String mysalary;
	
	@Autowired
    private MyTestBean myTestBean;
	
	
	/**
	 * 
@RefreshScope
 * 有人做过实验，说consul作为配置中心，修改值后，@Value的对象值不变，还是之前的，@ConfigurationProperties对象的值就变了，是最新的。
 * 其实是没有加@RefreshScope注解导致的。
	 * @return
	 */
	@GetMapping(value="/queryValue")
	public String queryValue() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		
		String message="Value方式返回：myname=["+myname+"],myaddress=["+myaddress+"],mysalary=["+mysalary+"] ";
		message+="<br>";
		message+="对象方式返回：myTestBean="+myTestBean.toPrint();
		System.out.println(message);
		return message;
	}
	
}
