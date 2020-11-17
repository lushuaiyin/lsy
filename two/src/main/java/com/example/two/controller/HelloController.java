package com.example.two.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@Component
@RestController
//@RequestMapping("/beat")
public class HelloController {
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	
//	@Autowired
//	private ComConfig conf;

	@GetMapping(value="/hello")
	public String hello() {
		String[] colorArr=new String[] {"red","blue","green","pink","gray"};
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime startTime = LocalDateTime.now();
		String message="hello !  I am   <span style=\"color:"+colorArr[1]+"\">["+servername+":"+ip+":"+port+"]</span>"+"..."+LocalDateTime.now().format(df);
		System.out.println(message);
		return message;
	}
}
