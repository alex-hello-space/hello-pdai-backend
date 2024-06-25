package yy.java.backend.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static yy.java.backend.io.InputStreams.FILE_PATH_4READ;

/**
 * NIO（New I/O）是一种可以替代标准 I/O 的 I/O API。它提供了一种更加灵活的 I/O 操作方式，
 * 支持面向缓冲区的、基于通道的 I/O 操作。
 *
 * <p>在 Java NIO 中，通道（Channel）相当于操作系统内核空间的缓冲区，而缓冲区（Buffer）则对应
 * 于用户空间中的用户缓冲区。</p>
 *
 * <ul>
 *     <li>1. 通道（Channel）是全双工的，支持双向传输，可以是读缓冲区或网络缓冲区。</li>
 *     <li>2. 缓冲区（Buffer）分为两种类型：分为堆内存（HeapBuffer）和堆外内存（DirectBuffer），这是通过 malloc() 分配出来的用户态内存。</li>
 * </ul>
 *
 * <p>堆外内存（DirectBuffer）在使用后需要应用程序手动回收，而堆内存（HeapBuffer）的数据在 GC 时可能会被自动回收。因此，在使用 HeapBuffer 读写数据时，
 * 为了避免缓冲区数据因为 GC 而丢失，NIO 会先把 HeapBuffer 内部的数据拷贝到一个临时的 DirectBuffer 中的本地内存（native memory），这个拷贝涉及到 sun.misc.Unsafe.copyMemory() 的调用，
 * 背后的实现原理与 memcpy() 类似。 最后，将临时生成的 DirectBuffer 内部的数据的内存地址传给 I/O 调用函数，这样就避免了再去访问 Java 对象处理 I/O 读写。</p>
 *
 * <p>更多关于 I/O 多路复用的信息，可以参考：
 * <a href="https://pdai.tech/md/java/io/java-io-nio-select-epoll.html#java%E5%AE%9E%E4%BE%8B%E6%94%B9%E8%BF%9B">io多路复用</a></p>
 *
 * @author yyHuangfu
 * @create 2024/6/24
 * @description 描述了 Java NIO 的基本概念和工作机制。
 */
public class NIOs {
    public static String FILE_PATH_4RW = "C:\\Users\\alex\\IdeaProjects\\hello-pdai-backend\\backend-java\\src\\yy\\java\\resource\\io\\io_nio_test_file";
}

/**
 * 1. 通道（Channel）：通道是双向的，可以从通道读取数据，也可以写数据到通道。
 * 2. 缓冲区（Buffer）：缓冲区本质上是一个可以写入数据，然后可以从中读取数据的内存块。
 */
class ChannelsAndBuffers {
    public static void main(String[] args) {
        try {
            RandomAccessFile file = new RandomAccessFile(FILE_PATH_4READ, "r");
            FileChannel fileChannel = file.getChannel();
            // capacity: 最大容量；limit:还可以读写的字节数；position: 当前已经读写的字节数
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (fileChannel.read(buffer) > 0) {
                // flip()方法将limit设置为当前position，并将 position设置为0。
                // 也就是说，flip()方法将缓冲区从写模式切换到读模式。
                buffer.flip(); // Prepare the buffer to be drained
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get()); // Read 1 byte at a time
                }
                // clear() 方法来清空缓冲区，此时 position 和 limit 都被设置为最初位置。
                buffer.clear(); // Empty buffer to get it ready for filling
            }
            fileChannel.close();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readDataFromSocketChannel(SocketChannel sChannel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder data = new StringBuilder();

        while (true) {
            // 重置buffer
            buffer.clear();
            // 从通道读取数据到缓冲区, 一次最多读取buffer的大小，1024字节
            int n = sChannel.read(buffer);
            if (n == -1) {
                // 读取完毕,退出循环
                break;
            }
            // 切换buffer为读模式
            buffer.flip();
            // limit为当前读取的字节数
            int limit = buffer.limit();
            char[] dst = new char[limit];
            for (int i = 0; i < limit; i++) {
                dst[i] = (char) buffer.get(i); // 因为英文占用一个字节，所以这里不会丢失数据
            }
            data.append(dst);
            buffer.clear();
        }
        return data.toString();
    }
}

/**
 * 3.选择器（Selector）: 是 Java NIO 中能够检测一到多个 NIO 通道，并能够知晓通道是否为诸如读写事件做好准备的组件。
 * 这样，一个单独的线程可以管理多个 channel，从而管理多个网络连接。
 */
class Selectors {


    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        // 通道必须配置为非阻塞模式，否则使用选择器就没有任何意义了，因为如果通道在某个事件上被阻塞，
        // 那么服务器就不能响应其它事件，必须等待这个事件处理完毕才能去处理其它事件，显然这和选择器的作用背道而驰。
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.configureBlocking(false);
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        ServerSocket serverSocket = ssChannel.socket();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
        serverSocket.bind(address);

        while (true) {
            int readyChannels = selector.select(1000); // 阻塞等待，直到有通道准备好
            if (readyChannels == 0) {
                // 超时，没有通道准备好
                continue;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                // isReadable()：这个方法用来检测通道是否准备好读取数据。如果返回true，则表示通道中的数据可以被读取，即通道的输入缓冲区中有数据可供读取。
                //通常用于SocketChannel或DatagramChannel等可以读取数据的通道。
                //isWritable()：这个方法用来检测通道是否准备好写入数据。如果返回true，则表示可以向通道写入数据，即通道的输出缓冲区有足够空间来接收新数据。
                //通常用于需要写入数据到通道的场景，如SocketChannel的写操作。
                //isAcceptable()：这个方法用来检测服务器套接字通道（ServerSocketChannel）是否准备好接受新的连接。如果返回true，则表示有新的客户端尝试连接到服务器。
                //这个方法专门用于ServerSocketChannel，因为只有服务器套接字通道需要接受来自客户端的连接。
                SelectionKey key = keyIterator.next();
                if (key.isValid() && key.isAcceptable()) { // 先accept, 再read
                    ServerSocketChannel ssChannel1 = (ServerSocketChannel) key.channel();

                    // 服务器会为每个新连接创建一个 SocketChannel
                    SocketChannel sChannel = ssChannel1.accept();
                    sChannel.configureBlocking(false);

                    // 这个新连接主要用于从客户端读取数据
                    sChannel.register(selector, SelectionKey.OP_READ);

                } else if (key.isValid() && key.isReadable()) {
                    SocketChannel sChannel = (SocketChannel) key.channel();
                    System.out.println(ChannelsAndBuffers.readDataFromSocketChannel(sChannel));
                    sChannel.close();
                }
                //这个已经处理的readyKey一定要移除。如果不移除，就会一直存在在selector.selectedKeys集合中
                //待到下一次selector.select() > 0时，这个readyKey又会被处理一次
                keyIterator.remove();
            }
        }
    }
}

class NIOClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        OutputStream out = socket.getOutputStream();
        String s = "hello world";
        out.write(s.getBytes());
        out.close();
    }
}