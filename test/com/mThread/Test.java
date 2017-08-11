package com.mThread;

import java.util.Random;

public class Test {

	private volatile int tt = 0;
	
	@org.junit.Test	// volatile test
	public void test() {
		/*
		testM();
		testM();
		testM();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(tt);
		*/
		for (int i = 0; i < 100; i++) {
			System.out.println(RandomString.random(5));
		}
	
	}
	
	
	@SuppressWarnings("unused")
	private void testM() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(tt<50)
					System.out.println(Thread.currentThread().getName() + " : " + (tt++));
			}
		}).start();
	}
}
class RandomString {
	private static final char[] chars;
	
	static {
		StringBuffer buffer = new StringBuffer();
		for(char ch = 'a'; ch <= 'z'; ++ch)
			buffer.append(ch);
		for(char ch = 'A'; ch <= 'Z'; ++ch)
			buffer.append(ch);
		chars = buffer.toString().toCharArray();
		
	}
	
	public static String random(int length){
		if(length < 1)
			throw new IllegalArgumentException("length < 1: " + length);
		
		StringBuilder randomString = new StringBuilder();
		Random random = new Random();
		
		for(int i=0; i < length; i++){
			randomString.append(chars[random.nextInt(chars.length)]);			
		}
		return randomString.toString();
	}	
}