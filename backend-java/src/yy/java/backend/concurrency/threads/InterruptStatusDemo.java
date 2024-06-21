package yy.java.backend.concurrency.threads;

public class InterruptStatusDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    // 让线程睡眠一段时间，以便主线程有机会中断它
                    Thread.sleep(5000);
                } catch (InterruptedException e) {                              // 捕获异常后，中断状态会被清除
                    System.out.println("after catch InterruptedException, during sleep()");
                    threadIsInterrupted("打印中断状态。此时已被清除。");        // Thread.interrupted() 打印中断状态。此时已被清除。
                    Thread.currentThread().interrupt();                          // 再次打断该线程，设置为打断状态
                    System.out.println("after interrupt()");
                    threadIsInterrupted("打印中断状态。此时被打断。");          // Thread.isInterrupted() 打印中断状态。此时被打断。
                }
            }
            // 检查并清除中断状态  
            boolean interruptedStatus = Thread.interrupted();
            System.out.println("Interrupted status after Thread.interrupted(): " + interruptedStatus + " 打印中断状态并清除。");

            // 再次检查中断状态（此时应该已被清除）  
            threadIsInterrupted("再次检查中断状态（此时已被清除）");
        });

        thread.start();

        // 给线程一些时间开始执行，然后中断它  
        Thread.sleep(1000);
        thread.interrupt();
    }

    private static void threadIsInterrupted(String msg) {
        boolean isInterruptedStatus = Thread.currentThread().isInterrupted();
        msg = " " + msg;
        System.out.println("Interrupted status after Thread.currentThread().isInterrupted(): " + isInterruptedStatus + msg);
    }
}