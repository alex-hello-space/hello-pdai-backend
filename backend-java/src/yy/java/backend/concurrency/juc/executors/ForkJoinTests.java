package yy.java.backend.concurrency.juc.executors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @author yyHuangfu
 * @create 2024/6/19
 * @description
 *
 * 核心思想: 分治算法(Divide-and-Conquer)
 */
public class ForkJoinTests {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ForkJoinPool pool = new ForkJoinPool();
        // 采用Fork/Join来异步计算1+2+3+…+10000的结果
        ForkJoinTask<Integer> task = new SumTask(1, 10000);
        pool.submit(task);
        System.out.println(task.get());
    }

    static final class SumTask extends RecursiveTask<Integer> { // RecursiveTask<V> extends ForkJoinTask<V>
        private static final long serialVersionUID = 1L;

        final int start; //开始计算的数
        final int end;   //最后计算的数

        SumTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            //如果计算量小于1000，那么分配一个线程执行if中的代码块，并返回执行结果
            if (end - start < 1000) {
                System.out.println(Thread.currentThread().getName() + " 开始执行: " + start + "-" + end);
                int sum = 0;
                for (int i = start; i <= end; i++)
                    sum += i;
                return sum;
            }
            //如果计算量大于1000，那么拆分为两个任务
            SumTask task1 = new SumTask(start, (start + end) / 2);
            SumTask task2 = new SumTask((start + end) / 2 + 1, end);
            //执行任务
            task1.fork();
            task2.fork();
            //获取任务执行的结果
            return task1.join() + task2.join();
        }
    }
}
