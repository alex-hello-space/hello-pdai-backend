package yy.java.backend.io;

import com.google.gson.Gson;

import java.io.*;

/**
 * @author yyHuangfu
 * @create 2024/6/22
 * @description 序列化就是将一个对象转换成字节序列，方便存储和传输。
 */
public class SerializableAndTransient {
}

class SerializableDemo {
    static String objectFile = "C:\\Users\\alex\\IdeaProjects\\hello-pdai-backend\\backend-java\\src\\yy\\java\\resource\\io\\serializable_test_file";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        A a1 = new A(123, "abc");
        // 将对象写入文件
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(objectFile));
        objectOutputStream.writeObject(a1);
        objectOutputStream.close();

        // 文件中读取字节并查看对象
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(objectFile));
        A a2 = (A) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println(a2);
    }

    // 序列化的类需要实现 Serializable 接口，它只是一个标准，没有任何方法需要实现，但是如果不去实现它的话而进行序列化，会抛出异常。
    // 如果无Serializable：
    // Exception in thread "main" java.io.NotSerializableException: yy.java.backend.io.SerializableDemo$A
    private static class A implements Serializable {
        private int x;
        private String y;

        A(int x, String y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "x = " + x + "  " + "y = " + y;
        }
    }
}

class TransientDemo {
    static String objectFile = "C:\\Users\\alex\\IdeaProjects\\hello-pdai-backend\\backend-java\\src\\yy\\java\\resource\\io\\transient_test_file";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String[] array = new String[]{"abc"};
        B b1 = new B(123, array, array);
        // 将对象写入文件
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(objectFile));
        objectOutputStream.writeObject(b1);
        objectOutputStream.close();

        // 文件中读取字节并查看对象
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(objectFile));
        B b2 = (B) objectInputStream.readObject();
        objectInputStream.close();

        // 用json工具序列化,也不反序列化transient字段
        Gson gson = new Gson();
        String b3 = gson.toJson(b1);

        System.out.println(b2);
        System.out.println(b3);
    }

    private static class B implements Serializable {
        private int x;
        private transient Object[] y; // transient 关键字可以使一些属性不会被序列化。
        private Object[] z;


        B(int x, Object[] y, Object[] z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "x = " + x + " y = " + y + " z = " + z;
        }
    }
}
