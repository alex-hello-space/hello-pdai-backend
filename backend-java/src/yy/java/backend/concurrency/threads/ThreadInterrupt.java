package yy.java.backend.concurrency.threads;

/**
 * @author yyHuangfu
 * @create 2024/6/19
 * @description 看ReentrantLock中lock()的源码：
 * <pre><code>
 * public final void acquire(int arg) {
 *         if (!tryAcquire(arg) &&
 *             acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
 *             selfInterrupt();
 *         }
 * </code></pre><br/>
 * interrupt():<br/> 是 Thread 类的一个静态方法，用于请求中断线程。
 * 当线程被中断时，它会设置一个中断状态。线程可以通过检查 Thread.currentThread().isInterrupted() 来检测中断状态。
 * 线程在执行某些阻塞操作时，如果被中断，会抛出 InterruptedException。抛出此异常后，中断状态会被清除。
 * 被 interrupt 后，线程不会自动死亡。它只是收到了一个中断信号，线程可以选择处理这个信号，比如通过抛出异常或执行一些清理工作，然后继续执行或退出。
 */
public class ThreadInterrupt {

    public static void main(String[] args) {
//        Thread t = new NotStopThread();
//        Thread t = new RunningThread();
        Thread t = new ControlInterruptThread();
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
    }

    // 这通常是通过调用Thread.interrupted()或Thread.isInterrupted()来实现的。前者会清除中断状态（即将中断标志重置为false），而后者仅检查状态而不清除。

    static class NotStopThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                long start = System.currentTimeMillis();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // 这里把中断异常捕获了，所以线程不会停止，反而只是把sleep给打断了
                    // 也就是线程并不会sleep 10s，而是sleep 5s
                    System.out.println(Thread.currentThread() + "is Interrupted when it sleeps for" + (System.currentTimeMillis() - start) + "ms");
                    // 如果要打断线程，这里需要重新设置中断标志位
                    Thread.currentThread().interrupt();
                }
                System.out.println("running");
            }
        }
    }

    static class RunningThread extends Thread {
        // 这个线程不会被中断

        /**
         * 如果一个线程的 run() 方法执行一个无限循环，
         * 并且没有执行 sleep() 等会抛出 InterruptedException 的操作，那么调用线程的 interrupt() 方法就无法使线程提前结束。
         * 但是调用 interrupt() 方法会设置线程的中断标记，此时调用 interrupted() 方法会返回 true。
         * 因此可以在循环体中使用 interrupted() 方法来判断线程是否处于中断状态，从而提前结束线程。
         */
        @Override
        public void run() {
            while (true) {
                System.out.println("running");
            }
        }
    }

    static class ControlInterruptThread extends Thread {
        @Override
        public void run() {
            while (!interrupted()) {
                System.out.println("running");
            }
        }
    }
}
