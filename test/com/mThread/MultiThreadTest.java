package com.mThread;

import static org.junit.Assert.*;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MultiThreadTest {

	private static int NUM_THREAD = 4;		// 사용 스레드 갯수
	private static final int NUM_MAX_RANDOM = 100;	// 랜덤 값 범위
	
	private ExecutorService executorService;
	
	@Before
	public void initExecutor() {
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		System.err.println("MAX Available Processors : " + availableProcessors);
		
		if(NUM_THREAD > availableProcessors)
			NUM_THREAD = availableProcessors;
		executorService = Executors.newFixedThreadPool(NUM_THREAD);
	}
	
	@After
	public void closeExecutor() {
		executorService.shutdown();
		try {
			if(executorService.awaitTermination(2, TimeUnit.SECONDS))
				System.err.println("Thread pool is terminated successfully");
			else
				System.err.println("Thread pool is failed being terminated");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Ignore
	public void testDivide() {
		int howManyThread = NUM_THREAD;
		Integer[] exampleArray = getRandomArray(100, false);
		printDividedArray(exampleArray);
		
		MultiThread mthread = new MultiThread(howManyThread);
		Integer[] startingPoints = mthread.getStartingPoints(exampleArray.length, howManyThread);
		printDividedArray(startingPoints);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testEvalSum() {
		long timer_start, timer_end;
		
		int howManyThread = 4;
		int result1, result2, result3;
		Integer[] exampleArray = getRandomArray(100, true);
		//printDividedArray(exampleArray);
		
		MultiThread mthread = new MultiThread(howManyThread);
		
		// 멀티스레드 사용
		timer_start = System.currentTimeMillis();
		result1 = mthread.evalSum(exampleArray, executorService);
		timer_end = System.currentTimeMillis();
		System.out.println("\nResult1 : "+result1 + "\tTime required : " + (timer_end-timer_start) + "ms (Using MultiThread)");
		
		// 싱글스레드 사용
		timer_start = System.currentTimeMillis();
		result2 = getSumSingleThread(exampleArray);
		timer_end = System.currentTimeMillis();
		System.out.println("Result2 : "+result2 + "\tTime required : " + (timer_end-timer_start) + "ms (Using SingleThread)");
		
		/*
		// stream function -> 쓰레기 (너무느림)
		timer_start = System.currentTimeMillis();
		int result4 = Arrays.stream(exampleArray).parallel().reduce(0, (a, b) -> a + b);
		timer_end = System.currentTimeMillis();
		System.out.println("Result4 : "+result4 + "\tTime required : " + (timer_end-timer_start) + "ms (Using stream func)\n");
		*/
		
		// ForkJoin 사용
		timer_start = System.currentTimeMillis();
		ForkJoinTask<Integer> task = new ForkJoinSumTask(exampleArray);
		result3 = new ForkJoinPool().commonPool().invoke(task);
		timer_end = System.currentTimeMillis();
		System.out.println("Result3 : "+result3 + "\tTime required : " + (timer_end-timer_start) + "ms (Using ForkJoin)");
		
		int[] aaa = {1, 2, 3};
		int[] bbb = {1, 2, 4};
		
		assertEquals("ss",aaa,bbb);
		

		assertEquals(result1, result2);
	}
	
	/**	
	 * IS NOT USED
	@Ignore
	public void Array_callByReferenceTest() {
		class TestClass {
			Integer[] array;
			
			public TestClass (Integer[] array) {
				this.array = array;
			}
			
			public void run() {
				array[1] = 99;
			}
		}
		
		Integer[] array = new Integer[3];
		array[0] = 0;
		array[1] = 1;
		array[2] = 2;
		
		printDividedArray(array);
		
		TestClass tc = new TestClass(array);
		tc.run();
		
		printDividedArray(array);
		printDividedArray(tc.array);
	}
	*/
	
	/**
	 * 배열 합계 계산 (단일 스레드)
	 * 
	 * @param	array	계산할 배열
	 * @return	총 합계
	 */
	private int getSumSingleThread(Integer[] array) {
		int result = 0;
		for(Integer node : array)
			result += node;
		return result;
	}

	/** 
	 * 랜덤 배열 반환 
	 * 
	 * @param	size			배열 사이즈
	 * @param	isDuplicated	중복된 값 허용 여부
	 * @return	생성된 랜덤 배열
	 */
	private Integer[] getRandomArray(int size, boolean isDuplicated) {
		if(size <= 0) return null;
		if(!isDuplicated && (size > NUM_MAX_RANDOM)) size = NUM_MAX_RANDOM;
			
		Random rand = new Random();
		Integer[] randomArray = new Integer[size];
		
		for(int i=0; i<size; i++) {
			randomArray[i] = rand.nextInt(NUM_MAX_RANDOM);
			if(!isDuplicated) {
				for(int j=0; j<i; j++) {
					if(randomArray[i] == randomArray[j]) {
						i--;
						break;
					}
				}
			}
		}
		
		return randomArray;
	}
	
	/** 
	 * 배열 출력
	 * 
	 * @param	array	출력할 배열
	 */
	private <T> void printDividedArray(T[] array) {
		if(array == null) return;
		for(T node : array) 
			System.out.println(node);
		System.out.println();
	}
	
}

