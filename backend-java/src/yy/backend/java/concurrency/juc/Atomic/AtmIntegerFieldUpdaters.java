package yy.backend.java.concurrency.juc.Atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author yyHuangfu
 * @create 2024/6/17
 * @description
 */
public class AtmIntegerFieldUpdaters {

    public static void main(String[] args) {
        AtmIntegerFieldUpdaters tIA = new AtmIntegerFieldUpdaters();
        tIA.doIt();
    }

    public AtomicIntegerFieldUpdater<DataNeedUpdated> updater(String name) {
        return AtomicIntegerFieldUpdater.newUpdater(DataNeedUpdated.class, name);

    }

    public void doIt() {
        // 更新的字段必须是volatile修饰，且可访问，否则会报异常
        DataNeedUpdated data = new DataNeedUpdated();
        System.out.println("publicVar = " + updater("publicVar").getAndAdd(data, 2));
        // protected修饰的成员变量可以在同一个包中的其他类和所有子类中访问
//        System.out.println("protectedVar = "+updater("protectedVar").getAndAdd(data,2));
        // private修饰的成员变量只能在本类中访问，报异常IllegalAccessException
//        System.out.println("privateVar = "+updater("privateVar").getAndAdd(data,2));

        System.out.println("staticVar = "+updater("staticVar").getAndIncrement(data));//报java.lang.IllegalArgumentException
        /*
         * 下面报异常：must be integer
         * */
        //System.out.println("integerVar = "+updater("integerVar").getAndIncrement(data));
        //System.out.println("longVar = "+updater("longVar").getAndIncrement(data));
    }

    static class DataNeedUpdated {
        public volatile int publicVar = 3;
        protected volatile int protectedVar = 4;
        private volatile int privateVar = 5;

        public volatile static int staticVar = 10;
        //public  final int finalVar = 11;

        public volatile Integer integerVar = 19;
        public volatile Long longVar = 18L;

    }
}
