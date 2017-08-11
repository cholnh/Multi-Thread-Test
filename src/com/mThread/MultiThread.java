package com.mThread;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class MultiThread {
	
	private int howManyThread;

	public MultiThread(int howManyThread) {
		this.howManyThread = howManyThread;
	}
	
	/**
	 * 분담 구역 지정
	 * 
	 * @param	length	배열 길이
	 * @param	n		나눌 수
	 * @return	시작인덱스로 구성된 배열 (반환된 배열의 각 노드들은 시작되어야 할 인덱스를 가짐)
	 */
	public Integer[] getStartingPoints(int length, int n) {
		if(n <= 0) return null;
		
		int loc = 0;
		int size = n;
		Integer[] startingPoints = new Integer[n];
		
		for(int i=0; i<size; i++) {
			int quotient = length / n;
			
			startingPoints[i] = loc;
			loc += quotient;
			
			// eval logic
			length -= quotient;
			n--;
		}
		
		return startingPoints;
	}
	
	/**
	 * 배열 합계 계산 (다중 스레드)
	 * 
	 * @param	array			계산할 배열
	 * @param	executorService	스레드풀
	 * @return	총 합계
	 */
	public int evalSum(Integer[] array, ExecutorService executorService) {
		CompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(executorService);	// executor + blocking queue -> 아래 take() 부분 참조

		Integer[] startingPoints = getStartingPoints(array.length, howManyThread);
		
		/* 결과값 받는 코드 */
		Future<Integer> totalFuture = executorService.submit(new Callable<Integer>() {
			
			@Override
			public Integer call() throws Exception {
				int totalSum = 0;
				try {
					for(int i=0; i<startingPoints.length; i++) {
						Future<Integer> future = completionService.take();	// take : 여러 FutureTask가 있는 블록 큐를 돌면서 끝난 녀석이 있는지 확인
						//System.out.println("도착 : " + future.get());
						totalSum += future.get();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				return totalSum;
			}
		});

		/* 스레드 주입 */
		for(int i=0; i<startingPoints.length; i++) {
			int start = startingPoints[i];
			int end = (i == howManyThread-1) ? array.length : startingPoints[i+1];
			SumThread thread = new SumThread(start, end, array);
			
			//futureList.add(executorService.submit(thread)); // pending Completion
			completionService.submit(thread);	// non pending completion
		}
		
	
		/* future.get()은 pending Completion이므로 값이 리턴될 때 까지 무한정 대기하기 때문에 느림.
		 *
		ArrayList<Future<Integer>> futureList = new ArrayList<>();
		
		for(Future<Integer> future : futureList) {
			try {
				totalSum += future.get();	// get : pending Completion
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		*/

		/* future.get()을 다른 스레드로 받으려는 안좋은 시도 -> 어차피 future.get()은 먼저 완료된 작업을 파악하지 못함 -> (해결책) ExecutorCompletionService
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int totalSum = 0;
				for(Future<Integer> future : futureList) {
					try {
						totalSum += future.get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
				System.out.println(totalSum);
			}
		}).start();
		*/
		
		int returnVal = 0;
		
		try {
			returnVal = totalFuture.get();	// 결과 값은 최종적으로 하나이기 때문에 future 사용
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return returnVal;
	}
	
	/**
	 * IS NOT USED
	public Integer[] getDividedArray(int startingPoint, int endPoint, Integer[] array) {
		int index = 0;
		Integer[] dividedArray = new Integer[endPoint - startingPoint];
		
		for(int i=startingPoint; i<endPoint; i++) {
			dividedArray[index++] = array[i];
		}
		
		return dividedArray;
	}
	*/
}

/**
 * 스레드 클래스
 * 
 * array의 범위 : startPoint ~ endPoint
 * 지정된 범위만큼의 값을 모두 더하여 반환
 * */
class SumThread implements Callable<Integer> {
	int startingPoint, endPoint;
	Integer[] array;
	
	public SumThread(int startingPoint, int endPoint, Integer[] array) {
		this.startingPoint = startingPoint;
		this.endPoint = endPoint;
		this.array = array;
	}
	
	@Override
	public Integer call() throws Exception{
		int result = 0;
		for(int i=startingPoint; i<endPoint; i++) {
			result += array[i];
		}
		System.out.println(Thread.currentThread().getName() + "\t is evaluating ["+startingPoint+"번째~ "+endPoint+"번째]\t- return val : " + result);
		return result;
	}
}
