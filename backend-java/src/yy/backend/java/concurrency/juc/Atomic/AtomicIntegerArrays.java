package yy.backend.java.concurrency.juc.Atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 */
public class AtomicIntegerArrays {
    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerArray array = new AtomicIntegerArray(new int[] { 0, 0 });
        System.out.println(array);
        System.out.println(array.getAndAdd(1, 2));
        System.out.println(array);
    }
}
