package com.example.gate.util;

import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * 获取环境变量值
 * 
 * @author lsy
 *
 */
public class EnvPropertyUtil {
	
	/**
	 * java -jar one.jar abc=haha启动时指定的值
	 * 
	 * 这个在eclipse的run configurations的program argumengts就可以设置，执行main函数就可打印：
	 * main方法接受传值：abc=haha
	 * 也就是说args参数可以是任何值，为了传键值对，最好用分隔符分开，方便我们解析数据。
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		for(int i=0;i<args.length;i++) {
			System.out.println("main方法接受传值："+args[i]);
		}
		
		getEnvFromD("xyz");
	}
	
	/**
	 * 通过java -jar one.jar -Dxyz=456启动时指定的值
	 * 
	 * @return
	 */
	public static String getEnvFromD(String key) {
		String res=null;
		try {
			Properties properties = System.getProperties();
			Set<Map.Entry<Object, Object>> set = properties.entrySet();
			for (Map.Entry<Object, Object> objectObjectEntry : set) {
				String temKey=(objectObjectEntry.getKey()==null)?"":((String)objectObjectEntry.getKey());
				String temValue=(objectObjectEntry.getValue()==null)?"":((String) objectObjectEntry.getValue());
//	        System.out.println(temKey + ":" + temValue);
			    if(key!=null && key.trim().equals(temKey)) {
			    	res=temValue;
			    	break;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    System.out.println(key+" the -D value is ["+res+"]"); 
		
		return res;
	}
	
	
	/**
	 * 在Linux下使用export XXYY=123指定的值。
	 * 
	 * @return
	 */
	public static String getEnvFromExport(String key) {
		String res = null;
		try {
			Map<String, String> map = System.getenv();
			Set<Map.Entry<String, String>> entries = map.entrySet();
			for (Map.Entry<String, String> entry : entries) {
				String temKey=entry.getKey();
				String temValue=entry.getValue();
				System.out.println(entry.getKey() + ":" + entry.getValue());
				if(key!=null && key.trim().equals(temKey)) {
			    	res=temValue;
			    	break;
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(key+" the Export value is ["+res+"]"); 
		
		return res;
	}
	
}
