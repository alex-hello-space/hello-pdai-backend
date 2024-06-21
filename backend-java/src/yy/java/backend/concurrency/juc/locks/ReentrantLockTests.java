package yy.java.backend.concurrency.juc.locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yyHuangfu
 * @create 2024/6/19
 * @description
 */
public class ReentrantLockTests {

    public static void main(String[] args) throws InterruptedException {
        Lock lock = new ReentrantLock(true);

        MyThread t1 = new MyThread("t1", lock);
        MyThread t2 = new MyThread("t2", lock);
        MyThread t3 = new MyThread("t3", lock);
        t1.start();
        t2.start();
        t3.start();
    }

    static class MyThread extends Thread {
        private Lock lock;

        public MyThread(String name, Lock lock) {
            super(name);
            this.lock = lock;
        }

        public void run() {
            System.out.println(Thread.currentThread() + "try to lock");
            // 尝试去获得锁
            lock.lock(); // 异步执行到这里时，变为同步执行。等待锁释放。
            try {
                System.out.println(Thread.currentThread() + " get lock, running");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println(Thread.currentThread() + " unlock");
                lock.unlock();
            }
        }
    }
}
