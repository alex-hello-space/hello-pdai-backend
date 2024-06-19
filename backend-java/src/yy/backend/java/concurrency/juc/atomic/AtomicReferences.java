package yy.backend.java.concurrency.juc.atomic;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 */
public class AtomicReferences {

    public static void main(String[] args) {

        // 创建两个Person对象，它们的id分别是101和102。
        Person p1 = new Person(101);
        Person p2 = new Person(102);
        // 新建AtomicReference对象，初始化它的值为p1对象
        AtomicReference ar = new AtomicReference(p1);
        // ASR和AMR和AR的区别是，ASR和AMR可以解决ABA问题。
        AtomicStampedReference asr = new AtomicStampedReference(p1, 0); // 内部使用Pair来存储元素值及其版本号
        AtomicMarkableReference amr = new AtomicMarkableReference(p1, false); // 内部使用Pair来存储元素值及其标记

        // 通过CAS设置ar。如果ar的值为p1的话，则将其设置为p2。
        ar.compareAndSet(p1, p2);
        asr.compareAndSet(p1, p2, asr.getStamp(), asr.getStamp() + 1);
        amr.compareAndSet(p1, p2, false, true);

        Person p3 = (Person) ar.get();
        System.out.println("p3 is " + p3);

        Person p3v = (Person) asr.get(new int[1]);
        System.out.println("p3v is " + p3v + ", stamp is " + asr.getStamp());

        Person p3m = (Person) amr.get(new boolean[1]);
        System.out.println("p3m is " + p3m + ", mark is " + amr.isMarked());
        System.out.println("p3.equals(p1)=" + p3.equals(p1));
    }

    static class Person {
        volatile long id;

        public Person(long id) {
            this.id = id;
        }

        public String toString() {
            return "id:" + id;
        }
    }
}
