package com.mThread;

import java.util.concurrent.RecursiveTask;

public class ForkJoinSumTask extends RecursiveTask<Integer>{
	private static final long serialVersionUID = -7993898893664553329L;
	
	static final int SPLIT_THRESHOLD = 10;	// 분할 한계점
	Integer[] values;	// 받은 배열을 여러개의 작은 단위로 분할하여(ForkJoin) 병렬로 계산  
	int startPos;
	int endPos;

	public ForkJoinSumTask(Integer[] values) {
		this(values, 0, values.length);
	}

	public ForkJoinSumTask(Integer[] values, int startPos, int endPos) {
		this.values = values;
		this.startPos = startPos;
		this.endPos = endPos;
	}

    @Override
    protected Integer compute() {
    	int computeTargetValueLength = endPos - startPos;
    	if (computeTargetValueLength <= SPLIT_THRESHOLD) {	// 더이상의 분할이 필요 없을 때 
    		return sumValuesSequentially();	// 분할을 진행하다가, 한계점에 다다르면 순차계산으로 넘어감
    	}
    	int splitPosition = startPos + computeTargetValueLength/2;
    	ForkJoinSumTask left = new ForkJoinSumTask(values, startPos, splitPosition);
    	left.fork();	// Thread pool에 추가하여 실행 요청(실제 실행될 때 다시 compute 호출됨, 그때 필요하면 다시 분할이 됨)

    	ForkJoinSumTask right = new ForkJoinSumTask(values, splitPosition, endPos);
    	int rightTaskResult = right.compute();	// Recursive 호출 개념 (내부에서 필요하면 다시 분할)
    	int leftTaskResult = left.join();

    	return leftTaskResult + rightTaskResult;	// Merge
    }
 
    private int sumValuesSequentially() {
    	int sum = 0;
    	//sum = Arrays.stream(values, startPos, endPos).reduce(0, (a, b) -> a + b);	// stream 연산 -> 느림
		for(int i=startPos; i<endPos; i++) {
			sum += values[i];
		}
    	//System.out.println(Thread.currentThread().getName() + ", startPos=" + startPos + ", endPos=" + endPos + ", sum=" + sum);
    	return sum;
    }
}
