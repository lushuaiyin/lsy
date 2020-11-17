package com.example.one.tmp;

/**
 * 断言默认不执行的，需要修改JVM参数
 * java -ea AssertTest 这是执行
 * java -da AssertTest 不执行（默认）
 * @author lsy
 *
 */
public class AssertTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testAssert("");
		testAssert2(null);
	}

	public static void testAssert(String param) {
		System.out.println("here..........start");
		assert (param!=null);
		System.out.println("here..........end");
	}
	
	public static void testAssert2(String param) {
		System.out.println("here..........start");
		assert (param!=null):"变量param为空导致异常了！";
		System.out.println("here..........end");
	}
}
