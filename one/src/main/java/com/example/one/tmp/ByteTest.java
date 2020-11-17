package com.example.one.tmp;

import java.nio.charset.Charset;
/**
 * 
 * GBK     英文数字1byte,汉字2byte
 * UTF-8   英文数字1byte,汉字3byte
 * Unicode 英文数字2byte,汉字2byte
 * 
 * 空格的的byte存储是32（16进制是20）
 * 但是byte默认值是0，这个值不是空格！就是byte的默认值，好比boolean的默认值是false。
 * 在UltraEdit用16进制编辑器可以看到这个0显示成一个点"."，用文本编辑器看到的是空白，有点像空格的效果。
 * 
 * 
 * byte[] huanhang="\n".getBytes();//换行的byte是 10，16进制是0A
 * byte[] huanhang22="\r".getBytes();//换行的byte是 13，16进制是0D
 * 
 * 用UltraEdit可以看文本的16进制内容（ctrl+H快捷键）
 * 在换行的地方，可以看到0D 0A，对应的就是\r\n换行符。在UltraEdit用16进制编辑器看到的也是一个点"."
 * 
 * 
 * @author lsy
 *
 */
public class ByteTest {

	public static void main(String[] args) {
		
		try {
			// TODO Auto-generated method stub
			byte[] huanhang="\n".getBytes();//换行的byte是 10，16进制是0A
			byte[] huanhang22="\r".getBytes();//换行的byte是 13，16进制是0D
			
			byte[] aa=new byte[4];
			System.out.println("aalen=="+aa.length);
			System.out.println("aabb=="+aa);
			System.out.println("aalenstr=="+new String(aa)+"==");
			byte[] kongge="    ".getBytes();
			String sam="ab中";//ab中
			byte[] arr1=sam.getBytes();
			System.out.println("arr1=="+arr1.length);
			
			byte[] arr2=sam.getBytes("UTF-8");
			System.out.println("arr2=="+arr2.length);
			System.out.println("arr2u2g=="+new String(arr2,"GBK"));
			
			byte[] arr3=sam.getBytes("GBK");
			System.out.println("arr3=="+arr3.length);
			System.out.println("arr3g2u=="+new String(arr3,"UTF-8"));
			
			String s1="  12";
			String s2="12  ";
			byte[] b1=s1.getBytes("UTF-8");
			byte[] b2=s2.getBytes("UTF-8");
			System.out.println("b1=="+new String(b1)+"==");
			System.out.println("b2=="+new String(b2)+"==");
			
			String ssstr=null;
			System.out.println("ssstr=="+ssstr.getBytes());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
