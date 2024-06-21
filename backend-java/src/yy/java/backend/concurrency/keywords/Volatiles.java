package yy.java.backend.concurrency.keywords;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author yyHuangfu
 * @create 2024/6/15
 * @description
 *  【可见性】在Java中，volatile关键字用于保证变量的可见性。在你的代码中，stop变量没有被声明为volatile，
 *  所以在"Thread A"中可能看不到主线程中对stop变量的修改。当主线程改变stop的值为true时，
 *  "Thread A"可能仍然看到stop为false，因为这个值可能被缓存在了线程的本地内存中，而不是从主内存中读取。
 *  这就是为什么循环可能会一直执行。为了解决这个问题，你可以将stop变量声明为volatile，
 *  这样每次读取stop的值时都会直接从主内存中读取，而不是从线程的本地内存。这样当主线程改变stop的值时，
 *  "Thread A"能立即看到这个改变，从而跳出循环。
 */
public class Volatiles {
    // 可见性
    private static boolean stop = false;

    public static void main(String[] args) {
        // Thread-A
        new Thread("Thread A") {
            @Override
            public void run() {
                while (!stop) {
                }
                System.out.println(Thread.currentThread() + " stopped");
            }
        }.start();

        // Thread-main
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(Thread.currentThread() + " after 1 seconds");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stop = true;}
        // stop set完main函数就退出了，但是stop变量还在线程的高速缓存中，
        // Thread-A就看不到该变量的变化，所以Thread-A就一直在运行。
}
