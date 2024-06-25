package yy.java.backend.io;

/**
 * @author yyHuangfu
 * @create 2024/6/19
 * @description
 */
public class DataBasicKnowledge {

}

class DataConvert {
    byte b = 10; // byte-8, int-32
    int i = b; // 自动转换

    int i2 = 300;
    byte b2 = (byte) i2; // 强制转换，但这里会导致数据丢失，因为300超出了byte的范围

    char c = 'a'; // 英文占用一个字节，转换为byte不会丢失数据
    byte b3 = (byte) c;
    char c1 = '好';
    byte b4 = (byte) c1; // 中文字符占用两个字节，转换为byte会丢失数据

    public static void main(String[] args) {
        DataConvert dataConvert = new DataConvert();
        System.out.println("i in binary: " + Integer.toBinaryString(dataConvert.i));
        System.out.println("b in binary: " + Integer.toBinaryString(dataConvert.b));
        System.out.println(dataConvert.i + " " + dataConvert.b + "\n");

        System.out.println("b in binary: " + Integer.toBinaryString(dataConvert.b2));
        System.out.println("i in binary: " + Integer.toBinaryString(dataConvert.i2));
        System.out.println(dataConvert.b2 + " " + dataConvert.i2);
        // 这是因为在Java中，当你试图将一个大的数值类型转换为一个小的数值类型时，如果大的数值类型的值超出了小的数值类型的范围，那么结果将会是一个“溢出”的值。
        // 在你的代码中，你试图将一个int类型的值300转换为一个byte类型的值。byte类型的值的范围是-128到127，而300超出了这个范围。因此，你得到的结果是一个溢出的值。
        // 这个溢出的值是如何计算的呢？首先，Java会将int类型的值转换为二进制形式。300的二进制形式是100101100。然后，Java会只保留最后8位（因为byte类型是8位），即00101100。这个二进制数转换为十进制就是44，所以你看到的输出是44。

        System.out.println("b3 : " + (char) dataConvert.b3);
        System.out.println("b4 : " + (char) dataConvert.b4); // 乱码，因为中文字符占用两个字节，转换为byte会丢失数据

    }
}