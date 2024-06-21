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
