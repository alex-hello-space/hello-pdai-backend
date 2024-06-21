package yy.java.backend.concurrency.juc.executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author yyHuangfu
 * @create 2024/6/19
 * @description 为什么线程池不允许使用Executors去创建? 推荐方式是什么?<br/>
 * 线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方式，这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。 说明：Executors各个方法的弊端：<br/>
 * <p>
 * 1. newFixedThreadPool和newSingleThreadExecutor: 主要问题是堆积的请求处理队列可能会耗费非常大的内存，甚至OOM。<br/>
 * 2. newCachedThreadPool和newScheduledThreadPool: 主要问题是线程数最大数是Integer.MAX_VALUE，可能会创建数量非常多的线程，甚至OOM。
 */
public class ThreadPoolExecutor {
    // commons-lang3包
    static class BasicThreadFactoryTests {
        public static void main(String[] args) throws InterruptedException {
            ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2,
                    new BasicThreadFactory.Builder()
                            .namingPattern("example-schedule-pool-%d")
                            .daemon(false) //执行线程是否是守护线程
                            .build());

            executorService.submit(() -> doTask(Thread.currentThread()));
            executorService.submit(() -> doTask(Thread.currentThread()));
            executorService.submit(() -> doTask(Thread.currentThread()));
            executorService.submit(() -> doTask(Thread.currentThread()));
            //gracefully shutdown
            executorService.shutdown();
        }
    }

    // com.google.guava包
    static class ThreadFactoryBuilderTests {
    }

    static void doTask(Thread thread) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(thread.getName() + " - Hello, ThreadPoolExecutor");
    }
}
