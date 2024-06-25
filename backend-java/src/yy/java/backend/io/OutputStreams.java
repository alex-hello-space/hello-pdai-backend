package yy.java.backend.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yyHuangfu
 * @create 2024/6/22
 * @description 比对 {@link InputStreams}学习。<br/>
 * 流对象进行读写操作都是按字节 ，一个字节一个字节的来读或写。<br/><br/>
 * 关键方法：<br/>
 * 1. {@link java.io.OutputStream#write(int)}<br/>
 * 写入一个字节，可以看到这里的参数是一个 int 类型，对应上面的读方法，int 类型的 32 位，只有低 8 位才写入，高 24 位将舍弃。
 */
public class OutputStreams {
    static String filePath = "C:\\Users\\alex\\IdeaProjects\\hello-pdai-backend\\backend-java\\src\\yy\\java\\resource\\io\\io_os_test_file";

    public static void main(String[] args) {

        try (OutputStream os = new FileOutputStream(filePath)) {
            String data = "写！ form OutputStreams";
            for (char c : data.toCharArray()) {
                System.out.print(c +",");
                os.write(c);
                // 实际写入文件后，中文部分为乱码，因为这里write实际做的是写入一个字节，
                // 因此中文字符char的高位字节被丢弃了。和IS同理，如果我们要处理字符而不是字节。
                // 我们应该用的是Writer而不是OutputStream。
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
