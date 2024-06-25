package yy.java.backend.io;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

/**
 * @author yyHuangfu
 * @create 2024/6/25
 * @description NettyServer
 *
 *  这段代码是一个使用Netty框架实现的简单客户端-服务器通信示例。Netty是一个高性能的网络编程框架，它提供了异步、事件驱动的网络应用程序框架和工具，用于快速开发可维护的高性能和高可靠性的网络服务器和客户端程序。
 *  <br><br>
 *  服务器端（NettyServer）描述：
 *  自举引导：通过ServerBootstrap类启动服务器，使用NioServerSocketChannelFactory作为服务器的通道工厂，这表明服务器是基于NIO（非阻塞IO）实现的。
 *  线程池：使用两个线程池，一个用于接受连接，另一个用于处理连接。
 *  消息处理：通过MessageHandler类处理接收到的消息。MessageHandler实现了SimpleChannelHandler，重写了messageReceived方法来处理接收到的消息，并构造响应消息。
 *  消息编码：服务器在发送消息之前，会将消息体和头部（消息长度）封装在一起，然后发送给客户端。
 *  <br><br>
 *  客户端（NettyClient）描述：
 *  类定义：NettyClient类定义了客户端的主要逻辑。
 *  发送消息：sendMessage方法用于发送消息到服务器。客户端首先连接到服务器，然后发送消息。
 *  消息编码：客户端在发送消息之前，会构造消息的头部（消息长度），然后将头部和消息体一起发送。
 *  接收响应：客户端接收服务器的响应，首先读取头部以确定消息体的长度，然后读取相应长度的消息体。
 * <br> <br>
 *  Netty与IO、NIO和AIO的区别：
 *  阻塞IO（BIO）：传统的阻塞IO模型中，每个连接都需要一个单独的线程来处理，这在高并发场景下会导致资源消耗巨大。
 *  非阻塞IO（NIO）：Java的NIO提供了非阻塞的IO操作，允许在单个线程中处理多个输入/输出通道。NIO的核心是缓冲区（Buffer）和通道（Channel），它允许更灵活地处理数据。
 *  异步IO（AIO）：AIO是Java 7引入的，提供了真正的异步IO操作，允许应用程序在执行IO操作时不被阻塞，而是在操作完成时接收通知。
 *  Netty：Netty是一个封装了NIO的框架，它提供了更高层次的抽象，如事件循环、管道和处理器，使得网络编程更加简单和高效。Netty还支持多种协议，可以很容易地扩展和定制。
 *
 */
public class NettyServer {

    private static int HEADER_LENGTH = 4;

    public static void main(String[] args) {
        try {
            new NettyServer().bind(1088);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bind(int port) {
        // 服务端启动引导-自举
        ServerBootstrap b = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // 构造对应的pipeline
        b.setPipelineFactory(new ChannelPipelineFactory() {

            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipelines = Channels.pipeline();
                pipelines.addLast(MessageHandler.class.getName(), new MessageHandler());
                return pipelines;
            }
        });
        // 监听端口号
        b.bind(new InetSocketAddress(port));
    }

    // 处理消息
    static class MessageHandler extends SimpleChannelHandler {

        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            // 接收客户端请求
            ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
            String message = new String(buffer.readBytes(buffer.readableBytes()).array(), "UTF-8");
            System.out.println("<服务端>收到内容=" + message);

            // 给客户端发送回执
            byte[] body = "服务端已收到".getBytes();
            byte[] header = ByteBuffer.allocate(HEADER_LENGTH).order(ByteOrder.BIG_ENDIAN).putInt(body.length).array();
            Channels.write(ctx.getChannel(), ChannelBuffers.wrappedBuffer(header, body));
            System.out.println("<服务端>发送回执,time=" + System.currentTimeMillis());

        }
    }

}

class NettyClient {

    private final ByteBuffer readHeader = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
    private final ByteBuffer writeHeader = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
    private SocketChannel channel;

    public void sendMessage(byte[] body) throws Exception {
        // 创建客户端通道
        channel = SocketChannel.open();
        channel.socket().setSoTimeout(60000);
        channel.connect(new InetSocketAddress("localhost", 1088));

        // 客户端发请求
        writeWithHeader(channel, body);

        // 接收服务端响应的信息
        readHeader.clear();
        read(channel, readHeader);
        int bodyLen = readHeader.getInt(0);// header中存放的是body的长度
        ByteBuffer bodyBuf = ByteBuffer.allocate(bodyLen).order(ByteOrder.BIG_ENDIAN);
        read(channel, bodyBuf);
        System.out.println("<客户端>收到响应内容: " + new String(bodyBuf.array(), "UTF-8") + ",长度:" + bodyLen);
    }

    private void writeWithHeader(SocketChannel channel, byte[] body) throws IOException {
        writeHeader.clear();
        writeHeader.putInt(body.length);
        writeHeader.flip();
        // channel.write(writeHeader);
        channel.write(ByteBuffer.wrap(body));
    }

    private void read(SocketChannel channel, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int r = channel.read(buffer);
            if (r == -1) {
                throw new IOException("end of stream when reading header");
            }
        }
    }

    public static void main(String[] args) {
        String body = "客户发的测试请求！";
        try {
            new NettyClient().sendMessage(body.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}