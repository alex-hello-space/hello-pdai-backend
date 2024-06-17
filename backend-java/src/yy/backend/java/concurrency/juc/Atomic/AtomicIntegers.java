package yy.backend.java.concurrency.juc.Atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 *
 * 1. volatile保证线程的可见性，多线程并发时，一个线程修改数据，可以保证其它线程立马看到修改后的值。
 * 2. CAS 保证数据更新的原子性。
 */
public class AtomicIntegers {

    private AtomicInteger count = new AtomicInteger(1);

    public void increment() {
        // 用的是unsafe的getAndIncrement方法
        // 通过CAS操作来保证线程安全，当compareAndSwapInt失败时，会一直重试，直到成功
        count.incrementAndGet();
    }

    // 使用 AtomicInteger 后，不需要加锁，也可以实现线程安全
    public int getCount() {
        return count.get();
    }
}
