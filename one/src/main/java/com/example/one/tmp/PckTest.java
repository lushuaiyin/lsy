package com.example.one.tmp;

import java.io.UnsupportedEncodingException;

/**
 * 
汉字的UTF-8编码长度:3
GBK编码长度:2
GB2312编码长度:2


对流的操作，如果要拼接报文，不要先转String再拼接！这样可能在某个位置的某个汉字截取了一半，
2段字符串再拼装那么就有乱码的汉字。
如果先把所有流的byte都组装起来再转化String就没有问题。



从10开始分成2段再拼装就乱码。因为中间有个汉字被截断了。

str==尊敬的客户欢迎您使用,aabyte长度=30
str1==尊敬的�
str2==��户欢迎您使用

从12隔断再拼装就没问题，因为是3的倍数，不会截断一个汉字。

str==尊敬的客户欢迎您使用,aabyte长度=30
str1==尊敬的客
str2==户欢迎您使用

15也不会乱码

str==尊敬的客户欢迎您使用,aabyte长度=30
str1==尊敬的客户
str2==欢迎您使用

 * @author lsy
 *
 */
public class PckTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String a="尊敬的客户欢迎您使用";
		try {
			byte [] aabyte = a.getBytes();
			int zlen=aabyte.length;
			String str=new String(aabyte,"utf-8");
			System.out.println("str=="+str+",aabyte长度="+zlen);
			
			int poslen=12;//12没有乱，10乱了
			byte [] arr1=new byte[poslen];
			byte [] arr2=new byte[zlen-poslen];
			System.arraycopy(aabyte, 0, arr1, 0, poslen);
			System.arraycopy(aabyte, poslen, arr2, 0, (zlen-poslen));
			
			String str1=new String(arr1,"utf-8");
			String str2=new String(arr2,"utf-8");
			System.out.println("str1=="+str1+"===");
			System.out.println("str2=="+str2+"===");
			
			
			//将2个byte数组拼接到一个新的byte数组中
			byte[] vv=addTwoByte(arr1,arr2);
			System.out.println("组装==="+new String(vv)+"===");
			
			
			//将2个byte数组拼接到一个新的byte数组中
			byte [] hhh=new byte[1];
//			byte [] hhh=null;
			hhh=addTwoByte(hhh,arr2);
			System.out.println("组装==="+new String(hhh)+"===");
			
			byte[] kong = "".getBytes();
			System.out.println("组装kong==="+new String(kong)+"===");
			
			byte[] copy1="12中".getBytes();
			byte[] all=new byte[16];
			boolean flag = setContentToByteArr(copy1,all,2);
			System.out.println("flag==="+flag+"==="+"组装==="+new String(all)+"===");
			
			
			byte[] src="12中".getBytes();
			byte[] srcopy=readSubByteByPos(src,0,5);
			System.out.println("srcopy.length==="+srcopy.length);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static byte[] addTwoByte(byte[] aa,byte[] bb) {
		if(aa==null||aa.length==0) {
			return bb;
		}
		if(bb==null||bb.length==0) {
			return aa;
		}
		int len=aa.length+bb.length;
		byte[] cc=new byte[len];
		System.arraycopy(aa, 0, cc, 0, aa.length);
		System.arraycopy(bb, 0, cc, aa.length, (bb.length));
		return cc;
	}
	
	public static boolean setContentToByteArr(byte[] aa,byte[] bb) {
		return setContentToByteArr(aa,bb,0);
	}
	
	/**
	 * 
	 * 把字节数组aa放入字节数组bb中，下标从pos开始。
	 * 如果空间不够则返回false。
	 * 
	 */
	public static boolean setContentToByteArr(byte[] aa,byte[] bb,int bbStart) {
		if(aa==null||aa.length==0) {//这种情况相当于不放，所以算成功
			return true;
		}
		if(bb==null||bb.length==0) {
			return false;
		}
		if(bbStart<0) {
			return false;
		}
		if(bb.length-bbStart<aa.length) {//从bbStart开始放入aa，空间不够
			return false;
		}
		System.arraycopy(aa, 0, bb, bbStart, (aa.length));
		return true;
	}
	
	//从aa的pos开始获取长度为len的子字节数组
	public static byte[] readSubByteByPos(byte[] aa,int pos,int len) {
		if(len<=0) {
			return null;
		}
		if(pos<0||pos>=aa.length) {
			return null;
		}
		byte[] copy=new byte[len];
		System.arraycopy(aa, pos, copy, 0, len);
		return copy;
	}

}
