package com.cashbang.service01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @Author: huangdj
 * @Date: 2020/6/11
 */
public class NewIoServer0 {

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(8080));
            while (true){
                SocketChannel socketChannel = serverSocketChannel.accept();
                if(socketChannel != null){
                    System.out.println("服务端收到请求啦");
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    Thread.sleep(10000000);
                    socketChannel.read(byteBuffer);
                    System.out.println(new String(byteBuffer.array()));
                    byteBuffer.flip();
                    socketChannel.write(byteBuffer);
                }else {
                    Thread.sleep(10000);
                    System.out.println("连接未就绪");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
