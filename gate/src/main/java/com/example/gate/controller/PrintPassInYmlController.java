package com.example.gate.controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * jasypt测试
 * 
 * @author lsy
 *
 */
@RestController
@RequestMapping("/jasypt")
public class PrintPassInYmlController {

	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	
	
	@Autowired
	StringEncryptor stringEncryptor;
	
	@Value("${mypass.pass1}")
	private String pass1;//yml中定义的是密文
	
	
	//查看程序跑起来后，yml中的密码是否是明文
	@RequestMapping(value = "/viewPass1",method = RequestMethod.GET)
    @ResponseBody
    public String viewPass1(){
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        String mess="viewPass ! 查看mypass.pass1的值是["+pass1+"]。response form  ["+servername+":"+ip+":"+port+"]"+"..."+dateStr;
        return mess;
        
    }
	
	/**
	 * 用jasypt把一个密码加密（秘钥用yml中定义的）
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/jasyptEncode",method = RequestMethod.GET)
    @ResponseBody
    public String jasyptEncode(@RequestParam(required=false) String param){
		System.out.println("jasyptEncode接收请求参数为==="+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        String newpass=stringEncryptor.encrypt(param);
        String mess="jasyptEncode ! 把["+param+"] 加密成["+newpass+"]。response form  ["+servername+":"+ip+":"+port+"]"+"..."+dateStr;
        return mess;
        
    }
	
	
	/**
	 * 用jasypt把一个密文解密，参数是密文，返回解密后的明文（使用的秘钥还是yml中定义的）
	 * 
	 * 使用post请求是因为密码有特殊字符，所以用post请求体传值
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/jasyptDecode",method = RequestMethod.POST)
    @ResponseBody
    public String jasyptDecode(@RequestBody String param){
		System.out.println("jasyptDecode接收请求参数为==="+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
		
        Map hm=new HashMap();
        hm.put("uname", "lsy");
        String realpass=stringEncryptor.decrypt(param);
        hm.put("upass", realpass);
        
        String mess="jasyptDecode ! 把["+param+"] 解密成["+realpass+"]。response form  ["+servername+":"+ip+":"+port+"]"+"..."+dateStr;
		System.out.println(mess);
		
        return "jasyptDecode response==="+hm.toString();
    }
	
}

