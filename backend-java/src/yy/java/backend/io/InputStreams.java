package yy.java.backend.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author yyHuangfu
 * @create 2024/6/22
 * @description
 * 这个input，是争对java程序来说的。java程序中的输入流，是指从外部读取数据到程序中的流。
 * 这个流可以是文件，也可以是网络连接，也可以是其他程序的输出流。因此input对应read。<br/>
 * 流对象进行读写操作都是按字节 ，一个字节一个字节的来读或写。
 * <br/><br/>
 * 关键方法：<br/> {@link InputStream#read()}: 取下一个字节的数据，如果没有则返回-1
 */
public class InputStreams {
    public static String FILE_PATH_4READ = "C:\\Users\\alex\\IdeaProjects\\hello-pdai-backend\\backend-java\\src\\yy\\java\\resource\\io\\io_is_test_file";

    public static void main(String[] args) {
        Charset charset = Charset.defaultCharset();
        System.out.println("Default Charset: " + charset);
        try (InputStream is = new FileInputStream(FILE_PATH_4READ)) { // try-with-resources
            int data = is.read();
            while(data != -1) {
                // 一个英文占一个byte，因此每次读取一个字节，就是一个英文
                // 而源文件utf-8编码中，中文占用3个字节。因此出现了乱码。
                // 注意的是：is本身就是字节流，想要更好的打印字符，需要转换成字符流。
                // reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                System.out.print((char) data);
                data = is.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
