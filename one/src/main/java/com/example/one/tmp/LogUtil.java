package com.example.one.tmp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 增加注释，测试git提交
 * 222
 * @author lsy
 *
 */
public class LogUtil {

	public static Logger logger = LoggerFactory.getLogger(LogUtil.class);
	
	public static void main(String[] args) {
		int i=0;
		while(i<1000) {
			i++;
			logger.info(i+"=="+"测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试");
		}
	}

}
