package com.example.one.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/limit")
public class LimitTestController {
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;

	@GetMapping(value="/sprint")
	public String sprint() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		String message="sprint !  I am   <span style=\"color:"+colorArr[0]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+timeNow;
		System.out.println(message);
		return message;
	}
	
	@GetMapping(value="/sprint2")
	public String sprint2() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//LocalDateTime startTime = LocalDateTime.now();
		String timeNow=LocalDateTime.now().format(df);
		String message="sprint2222 !  I am   <span style=\"color:"+colorArr[0]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+timeNow;
		System.out.println(message);
		return message;
	}
	
}
