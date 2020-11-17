package com.example.one.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadFileController {
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	

	/**
     * 录音文件
     * */
//    @RequestMapping("getSpeechListen")
	
//	@RequestMapping(value = "/getSpeechListen", method = RequestMethod.POST)
    @PostMapping(value="/getSpeechListen")
    @ResponseBody
    public String getSpeechListen(@RequestParam("file") MultipartFile file, 
    		HttpServletRequest request) throws IOException{

        if (file==null) {
            System.out.println("文件名为空");
            return getResultJson("fail","上传失败,file is null");
        }
        
     // 获取文件名
		String filename = file.getOriginalFilename();
		System.out.println("上传的文件名" + filename);
	
		// 获取文件的后缀名,因为文件名不能是文件的唯一标识，比如1.png 1.jpg
		String suffixName = filename.substring(filename.lastIndexOf("."));
		System.out.println("文件的后缀名" + suffixName);
	
		// 文件上传后的路径
		String filepath="D:\\Temp";
		File dest = new File(filepath +"\\\\"+ filename);
		
		try {  //MultipartFile 对象的transferTo方法，用于文件保存（效率和操作比原先用FileOutStream方便和高效）
			file.transferTo(dest);
			return getResultJson("success","上传成功");
		} catch (Exception e) {
			e.printStackTrace();
			return getResultJson("fail","上传异常."+e.getMessage());
		}

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
	
	public String getResultJson(String retCode,String retMessage) {
		Map hm=new HashMap();
        hm.put("retCode", retCode);
        hm.put("retMessage", retMessage);
        
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        hm.put("retTime", LocalDateTime.now().format(df));
        
        return hm.toString();
	}
	
	
	
}
