package yy.java.backend.concurrency.juc.locks;

import yy.java.backend.concurrency.threads.ThreadWaitNotify;

import java.util.concurrent.locks.LockSupport;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 * Thread wait/notify实现线程同步{@link ThreadWaitNotify}<br/>
 * wait/notify要求执行顺序，先wait后notify，否则会导致死锁。park/unpark不需要执行顺序，可以先unpark后park。
 * interrupt可以起到与unpark一样的作用。
 */
public class LockSupportTests {

    public static void main(String[] args) {
        Thread mainThread = Thread.currentThread();
        new UnparkThread(mainThread).start();
//        不管park和unpark的调用先后顺序，都能够正常运行
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("before park");
        // 获取许可
        LockSupport.park("ParkAndUnparkDemo"); // 把当前线程挂起
        System.out.println("after park");
    }

    static class UnparkThread extends Thread {
        private final Thread mainThread;

        public UnparkThread(Thread mainThread) {
            this.mainThread = mainThread;
        }

        public void run() {
            System.out.println("before unpark");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 获取blocker
            System.out.println("Blocker info " + LockSupport.getBlocker(mainThread));
            // 释放许可
            LockSupport.unpark(mainThread);
            // 休眠500ms，保证先执行park中的setBlocker(t, null);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 再次获取blocker
            System.out.println("Blocker info " + LockSupport.getBlocker(mainThread));
            System.out.println("after unpark");
        }
    }
}
