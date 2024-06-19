package yy.backend.java.concurrency.threads;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 * <br/>
 * synchronized 关键字和 Object.wait() 方法在Java中都涉及到线程的阻塞和等待机制，但它们在具体的使用和行为上有一些区别：<br/><br/>
 * synchronized：<br/>
 * 当一个线程尝试获取一个已经被其他线程持有的监视器锁（由 synchronized 块或方法提供）时，它会进入一个等待集合（也称为锁池）。
 * 在这个等待集合中，线程会释放CPU资源，进入阻塞状态，直到它能够获取到锁。<br/><br/>
 *  Object.wait()：<br/>
 * 当一个线程调用 wait() 方法时，它会释放与该对象相关联的监视器锁，并且进入该对象的等待集合（也称为等待池）。
 * 线程在等待池中会释放CPU资源，直到它被另一个线程通过调用 notify() 或 notifyAll() 方法唤醒。
 * 尽管两者都涉及到线程的阻塞和等待，但它们在一些关键方面有所不同：<br/><br/>
 * 锁的范围：synchronized 可以用于同步代码块或方法，而 wait() 必须在 synchronized 块内部调用。
 * 锁的释放：调用 wait() 时，当前线程会释放它持有的对象锁，而 synchronized 失败时，线程只是进入等待获取锁的状态，不会释放任何锁。
 * 锁的重新获取：wait() 方法被调用后，线程必须在某个时刻重新获取锁才能继续执行（通过 notify() 或 notifyAll()）。而 synchronized 则不需要显式的通知机制，线程会在锁可用时自动尝试获取。
 * 使用场景：synchronized 主要用于实现同步和互斥，而 wait() 和 notify() 主要用于线程间的协调和通信。<br/><br/>
 * wait和park的区别：<br/><br/>
 * 当线程调用 Thread.wait() 时，它必须在 synchronized 块中，并且会释放当前持有的监视器锁（对象锁）。<br/>
 * Unsafe.park 没有释放锁的内在行为，它仅仅是阻塞当前线程。如果线程在持有锁的情况下调用 Unsafe.park，它不会释放那个锁。<br/>
 * Thread.wait() 是面向所有Java程序员的标准方法，适用于大多数需要线程等待的场景。Unsafe.park 是一个更底层的操作，通常只在实现自定义同步器时使用，并且需要更仔细地管理锁和中断。
 */
public class ThreadWaitNotify {

    public static void main(String[] args) throws InterruptedException {
        MyThread myThread = new MyThread();
        synchronized (myThread) {
            try {
                myThread.start();
                // 主线程睡眠3s
                java.lang.Thread.sleep(3000);
                System.out.println("before wait");
                // 阻塞主线程
                myThread.wait();
                System.out.println("after wait");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class MyThread extends java.lang.Thread {
        public void run() {
            synchronized (this) {
                System.out.println("before notify");
                notify();
                System.out.println("after notify");
            }
        }
    }
}
