package com.example.gate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
//@RequestMapping("/beat")
public class BeatController {

	Logger logger = LoggerFactory.getLogger(BeatController.class);
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	

//	@Autowired
//	HttpServletRequest request;//获取request
	
	@GetMapping(value="/hello")
	public String hello() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		
		return "hello !  I am  hello  ["+servername+":"+ip+":"+port+"]"+"..."+timeNow;
	}
	
	
	@GetMapping(value="/testprint")
	public String testprint() {//获取request2  HttpServletRequest request222
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		
		//获取request3
//		HttpServletRequest request333 = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		
//		logger.info("testprint............request.getParameter=="+request.getParameter("param1"));
//		logger.info("testprint............request222.getParameter=="+request222.getParameter("param1"));
//		logger.info("testprint............request333.getParameter=="+request333.getParameter("param1"));
		
		return "hello !  I am  testprint ["+servername+":"+ip+":"+port+"]"+"..."+timeNow;
	}
}