package com.example.one.bean;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 在另一个类中使用@EnableConfigurationProperties(MyTestBean.class) 就注释掉@Component
 * @author lsy
 *
 *
 *ignoreUnknownFields = false告诉Spring Boot在有属性不能匹配到声明的域的时候抛出异常。
 *开发的时候很方便! prefix 用来选择哪个属性的prefix名字来绑定。
 */
//@Component
//@ConfigurationProperties(prefix = "mytest")
@ConfigurationProperties(prefix = "mytest", ignoreInvalidFields = true)
public class MyTestBean {

	private String myname;
	private String myaddress;
	private String mysalary;//测试ignoreInvalidFields，如果没有改属性，是否报错
	
	public String getMyname() {
		return myname;
	}
	public void setMyname(String myname) {
		this.myname = myname;
	}
	public String getMyaddress() {
		return myaddress;
	}
	public void setMyaddress(String myaddress) {
		this.myaddress = myaddress;
	}
	public String getMysalary() {
		return mysalary;
	}
	public void setMysalary(String mysalary) {
		this.mysalary = mysalary;
	}
	
	/*
	 * 在对象构建后打印一下.
	 * 经测试世纪打印：
	 * 
	 * MyTestBean toPrint： [myname=lsy, myaddress=bejing, mysalary=null]
	 * 
	 */
	@PostConstruct
	public String toPrint() {
		String str= " [myname=" + myname + ", myaddress=" + myaddress + ", mysalary=" + mysalary + "]";
		System.out.println("MyTestBean toPrint："+str);
		return str;
	}
	
	
	
}
