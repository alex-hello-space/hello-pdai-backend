package yy.java.backend.concurrency.juc;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 */

public class UnsafeExample {
    private static Unsafe unsafe;
    private static Object obj = new Object();
    private static long offset;
    private static int someIntField = 1;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null); // theUnsafe是静态的，所以传入null
            // 偏移量是用于 Unsafe 类的各种操作的参数，
            // 这些操作可以直接访问和修改字段的值，而无需通过常规的get和set方法。
            offset = staticObjectFieldOffset("obj");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // 使用 getAndSetObject 原子地设置对象字段的值
        Object oldVal = (Object) unsafe.getAndSetObject(UnsafeExample.class, offset, new Object());
        System.out.println("Old value: " + oldVal);

        // 使用 compareAndSwapObject 原子地比较并设置对象字段的值
        // 如果字段的当前值与预期值相等（即成功替换为新的值），则返回 true；
        // 如果字段的当前值与预期值不相等（即没有替换为新的值），则返回 false。
        Object expectedValue = new Object();
        boolean swapped = unsafe.compareAndSwapObject(UnsafeExample.class, offset, expectedValue, obj);
        System.out.println("expectedValue: " + expectedValue);
        System.out.println("Swapped: " + swapped);

        // 使用 getAndAddInt 原子地增加一个整数字段的值
        int oldValue = unsafe.getAndAddInt(UnsafeExample.class, intFieldOffset("someIntField"), 10);
        System.out.println("Old value: " + oldValue);
        System.out.println("Now value: " + someIntField);

        // 使用 putOrderedObject 无屏障地设置对象字段的值
        unsafe.putOrderedObject(UnsafeExample.class, offset, expectedValue);
        System.out.println("New value: " + obj);
    }

    // 静态字段的偏移量
    private static long staticObjectFieldOffset(String fieldName) {
        try {
            return unsafe.staticFieldOffset(UnsafeExample.class.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    // 静态字段的偏移量
    private static long intFieldOffset(String fieldName) {
        try {
            return unsafe.staticFieldOffset(UnsafeExample.class.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * park() 方法可以挂起当前线程，使其进入等待状态。它有两个参数，第一个参数是一个布尔值，表示是否要在等待时考虑线程的中断状态；
 * 第二个参数是一个长整型值，表示等待的时间（单位是纳秒）。如果第一个参数为 true，那么在等待过程中如果线程被中断，park() 方法会立即返回。
 * 如果第二个参数为 0，那么线程会无限期地等待，直到被 unpark() 方法唤醒。
 */
class UnsafeParkDemo {
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("Thread is going to park");
            unsafe.park(false, 0L); // Unsafe.park()方法不会释放任何锁!一般不和synchronized一起使用
            System.out.println("Thread is unparked");
        });

        // 启动线程
        thread.start();

        try {
            System.out.println("Main thread is going to sleep for 3s");
            Thread.sleep(3000);
            System.out.println("Main thread after sleep()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main thread is going to unpark the other thread");
        unsafe.unpark(thread);
    }
}
