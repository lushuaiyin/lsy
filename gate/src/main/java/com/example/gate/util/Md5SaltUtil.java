package com.example.gate.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


/**
 * MD5加盐加密算法
 * 4位盐，32位密码共36位长度
 * 
 * @author lsy
 *
 */
public class Md5SaltUtil {

	public static void main(String[] args) {

		//测试加密和验密
		for(int i=0;i<3;i++) {
			System.out.println("\r\n");
			//加密过程---------------
			String password = "111111";
			String dbpass = Md5SaltUtil.getEncryptedPwd(password);//加密后存放在数据库
			System.out.println("#######################");
			//验密过程---------------跟数据库密码比对
			String passwordValid="111111";//待验证的密码
			boolean falg= Md5SaltUtil.validPassword(passwordValid, dbpass);//用待验证的密码 和数据库的密码进行验密
			if(falg) {
				System.out.println(passwordValid+"验密通过.");
			}else {
				System.out.println(passwordValid+"验密失败........................");
			}
			
			//验密过程---------------跟数据库密码比对
			String passwordValidError="111111xxxx";//待验证的密码
			boolean falg2= Md5SaltUtil.validPassword(passwordValidError, dbpass);//用待验证的密码 和数据库的密码进行验密
			if(falg2) {
				System.out.println(passwordValidError+"验密通过.");
			}else {
				System.out.println(passwordValidError+"验密失败........................");
			}
		}
	}

	private static final String HEX_NUMS_STR = "0123456789ABCDEF";
	private static final Integer SALT_LENGTH = 2;
	private static final boolean isDebug = true;//是否打印debug日志

	/**
	 * 将16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] hexChars = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
		}
		return result;
	}
	
//	private static byte[] fromHex(String hex)
//    {
//        byte[] binary = new byte[hex.length() / 2];
//        for(int i = 0; i < binary.length; i++)
//        {
//            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
//        }
//        return binary;
//    }

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

//	private static String toHex(byte[] array)
//    {
//        BigInteger bi = new BigInteger(1, array);
//        String hex = bi.toString(16);
//        int paddingLength = (array.length * 2) - hex.length();
//        if(paddingLength > 0)
//            return String.format("%0" + paddingLength + "d", 0) + hex;
//        else
//            return hex;
//    }
	
	/**
	 * 验密
	 * 数据库的密文是：“盐+密文”。
	 * 从数据库的密码中取出盐，用这个盐和待验证密码，再走一遍加密的算法得到“盐+待验证密码的密文”，看结果和数据库密码是否相等。
	 * 
	 * @param password 待验证的密码
	 * @param passwordInDb 存储的密码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean validPassword(String password, String passwordInDb) {
		try {
			// 将16进制字符串格式口令转换成字节数组
			byte[] pwdInDb = hexStringToByte(passwordInDb);
			// 声明盐变量
			byte[] salt = new byte[SALT_LENGTH];
			// 将盐从数据库中保存的口令字节数组中提取出来
			System.arraycopy(pwdInDb, 0, salt, 0, SALT_LENGTH);
			if (isDebug) {
				//打印 
				String saltStr=byteToHexString(salt);
				System.out.println("【validPassword】  取出数据库密码["+passwordInDb+"]中的盐====saltStr长度==="+saltStr.length()+",value===【"+saltStr+"】");
			}
			// 创建消息摘要对象
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 将盐数据传入消息摘要对象
			md.update(salt);
			// 将口令的数据传给消息摘要对象
			md.update(password.getBytes("UTF-8"));
			// 生成输入口令的消息摘要
			byte[] digest = md.digest();
			
			
			// 因为要在口令的字节数组中存放盐，所以加上盐的字节长度
			byte[] pwdValid = new byte[digest.length + SALT_LENGTH];
			// 将盐的字节拷贝到生成的加密口令字节数组的前SALT_LENGTH个字节，以便在验证口令时取出盐
			System.arraycopy(salt, 0, pwdValid, 0, SALT_LENGTH);
			// 将消息摘要拷贝到加密口令字节数组从第SALT_LENGTH+1个字节开始的字节
			System.arraycopy(digest, 0, pwdValid, SALT_LENGTH, digest.length);
			String passwordValid = byteToHexString(pwdValid);
			if (isDebug) {
				//打印 
				String digestStr=byteToHexString(digest);
				System.out.println("【validPassword】  用数据库密码中的盐对待验证密码加盐加密====digestStr长度==="+digestStr.length()+",value==="+digestStr);
				System.out.println("【validPassword】  用数据库密码中的盐对待验证密码加盐加密最终结果====passwordValid长度==="+passwordValid.length()+",value==="+passwordValid);
				System.out.println("【validPassword】  比较待验证密码加密["+passwordValid+"],数据库密码["+passwordInDb+"]");
			}
			if(passwordValid!=null && passwordValid.equals(passwordInDb)) {
				return true;
			}else {
				return false;
			}
//			// 声明一个保存数据库中口令消息摘要的变量
//			byte[] digestInDb = new byte[pwdInDb.length - SALT_LENGTH];
//			// 取得数据库中口令的消息摘要
//			System.arraycopy(pwdInDb, SALT_LENGTH, digestInDb, 0, digestInDb.length);
//			// 比较根据输入口令生成的消息摘要和数据库中消息摘要是否相同
//			if (Arrays.equals(digest, digestInDb)) {
//				// 口令正确返回口令匹配消息
//				return true;
//			} else {
//				// 口令不正确返回口令不匹配消息
//				return false;
//			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 加密。
	 * 
	 * 随机生成一个盐。用盐和密码加密后得到密文。
	 * 最终返回 “盐+密文”。
	 * 
	 * @param password 待加密的密码
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getEncryptedPwd(String password){
		// 将字节数组格式加密后的口令转化为16进制字符串格式的口令
		String res=null;
		try {
			// 声明加密后的口令数组变量
			byte[] pwd = null;
			// 随机数生成器
			SecureRandom random = new SecureRandom();
			// 声明盐数组变量 SALT_LENGTH
			byte[] salt = new byte[SALT_LENGTH];
			// 将随机数放入盐变量中
			random.nextBytes(salt);
			if(isDebug) {
				//打印 
				String saltStr=byteToHexString(salt);
				System.out.println("【getEncryptedPwd】 ["+password+"]生成盐====saltStr长度==="+saltStr.length()+",value===【"+saltStr+"】");
			}
			// 声明消息摘要对象
			MessageDigest md = null;
			// 创建消息摘要
			md = MessageDigest.getInstance("MD5");
			// 将盐数据传入消息摘要对象
			md.update(salt);
			// 将口令的数据传给消息摘要对象
			md.update(password.getBytes("UTF-8"));
			// 获得消息摘要的字节数组
			byte[] digest = md.digest();
			
			if (isDebug) {
				//打印 
				String disgStr=byteToHexString(digest);
				System.out.println("【getEncryptedPwd】加盐后再加密====disgStr长度==="+disgStr.length()+",value==="+disgStr);
			}
			// 因为要在口令的字节数组中存放盐，所以加上盐的字节长度
			pwd = new byte[digest.length + SALT_LENGTH];
			// 将盐的字节拷贝到生成的加密口令字节数组的前SALT_LENGTH个字节，以便在验证口令时取出盐
			System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);
			// 将消息摘要拷贝到加密口令字节数组从第13个字节开始的字节
			System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);
			res = byteToHexString(pwd);
			if (isDebug) {
				System.out.println("【getEncryptedPwd】"+password+"加密后最终结果（盐加密码）长度："+res.length()+",密文==="+res);
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res=null;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res=null;
		}
		return res;
	}
}
