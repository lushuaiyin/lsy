package com.example.gate.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class StrUtil {
	public static void main(String[] args) {
		HashMap<String, Integer> map = new HashMap<>();
		map.put("wave16", 76);
		map.put("wave1", 92);
		map.put("wave10", 56);
		map.put("wave8", 86);
//		map.put("wave8", 12);
		// 按照 Key (名字)进行排序 ,并打印
		map.entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey())).forEach(System.out::println);
		System.out.println("map----------" + map.toString());
		String jjjj = JsonUtil.objectToString(map);
		System.out.println("jjjj----------" + jjjj);

		System.out.println("wave1".substring(4));
		
		System.out.println("-------分割线----------");
		// 按照value(分数) 进行排序,并打印
		map.entrySet().stream().sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue())).forEach(System.out::println);

	}

	
	//map排序的例子
	public void sortMapSample() {
		TreeMap hm = new TreeMap<String, String>(new Comparator() {
			public int compare(Object o1, Object o2) {
				// 如果有空值，直接返回0
				if (o1 == null || o2 == null)
					return 0;

				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		});
	}
}
