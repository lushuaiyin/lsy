package com.example.gate.util;

import java.security.MessageDigest;


/**
 * MD5加密工具
 * 
 * @author lsy
 *
 */
public class MD5Util {
	
	public static void main(String[] args){
		String  abc = "abcdjkhfd*";
		for(int i=0;i<10;i++) {
			System.out.println("md5加密后==="+MD5Util.string2MD5(abc));
		}
	}

	public static String string2MD5(String inStr) {
		if(inStr==null) {
			return null;
		}
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString().toUpperCase();
	}
}
