package yy.java.backend.concurrency.juc.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description AQS核心思想是，如果被请求的共享资源空闲，则将当前请求资源的线程设置为有效的工作线程，并且将共享资源设置为锁定状态。
 * 如果被请求的共享资源被占用，那么就需要一套线程阻塞等待以及被唤醒时锁分配的机制，这个机制AQS是用CLH队列锁实现的，
 * 即将暂时获取不到锁的线程加入到队列中。
 *
 * lock和condition await/signal类比synchronized和wait wait/notify：lock是保证动作的同步，condition用来唤醒和挂起线程。
 */
public class AQSerTests {

    public static void main(String[] args) throws InterruptedException {
        Depot depot = new Depot(500);
        new Producer(depot).produce(500);
        new Producer(depot).produce(200);
        new Consumer(depot).consume(500);
        new Consumer(depot).consume(200);
    }

    public static class Depot { // 仓库类
        private int size;
        private int capacity;
        private Lock lock;
        private Condition fullCondition;
        private Condition emptyCondition;

        public Depot(int capacity) {
            this.capacity = capacity;
            lock = new ReentrantLock();
            fullCondition = lock.newCondition();
            emptyCondition = lock.newCondition();
        }

        public void produce(int no) {
            lock.lock();
            int left = no;
            try {
                while (left > 0) {
                    while (size >= capacity) {
                        System.out.println(Thread.currentThread() + " before await");
                        fullCondition.await();
                        System.out.println(Thread.currentThread() + " after await");
                    }
                    int inc = (left + size) > capacity ? (capacity - size) : left;
                    left -= inc;
                    size += inc;
                    System.out.println("produce = " + inc + ", size = " + size);
                    emptyCondition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void consume(int no) {
            lock.lock();
            int left = no;
            try {
                while (left > 0) {
                    while (size <= 0) {
                        System.out.println(Thread.currentThread() + " before await");
                        emptyCondition.await();
                        System.out.println(Thread.currentThread() + " after await");
                    }
                    int dec = (size - left) > 0 ? left : size;
                    left -= dec;
                    size -= dec;
                    System.out.println("consume = " + dec + ", size = " + size);
                    fullCondition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class Consumer {
        private Depot depot;

        public Consumer(Depot depot) {
            this.depot = depot;
        }

        public void consume(int no) {
            new Thread(() -> depot.consume(no), no + " consume thread").start();
        }
    }

    static class Producer {
        private Depot depot;

        public Producer(Depot depot) {
            this.depot = depot;
        }

        public void produce(int no) {
            new Thread(() -> depot.produce(no), no + " produce thread").start();
        }
    }
}
