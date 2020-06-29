package com.cashbang.service01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: huangdj
 * @Date: 2020/6/11
 */
public class NewIoServer {

    static Selector selector;


    public static void main(String[] args) {
        try {
            selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);//设置为非阻塞
            serverSocketChannel.socket().bind(new InetSocketAddress(8080));//绑定一个端口
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//将连接事件注册到多路复用器上
            while (true){
                selector.select();//阻塞机制，只有事件到达的时候才会唤醒
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//有事件进入
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if(key.isAcceptable()){
                        handleAccept(key);
                    }else if(key.isReadable()){
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRead(SelectionKey selectionKet) {
        //之前已经将socketChannel注册了读事件
        SocketChannel socketChannel = (SocketChannel) selectionKet.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            socketChannel.read(byteBuffer);
            System.out.println("server receive msg:"+new String(byteBuffer.array()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAccept(SelectionKey selectionKey) {
        //77行将serverSocketChannel中的连接事件注册上，所以这里拿到的是serverSocketChannel
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        try {
            //一定会有一个连接
            System.out.println("来啦，老弟");
            SocketChannel socketChannel = serverSocketChannel.accept();
            //
            socketChannel.configureBlocking(false);
            Thread.sleep(10000);

            socketChannel.write(ByteBuffer.wrap("Hello Client,I'm NIO Server".getBytes()));
            //注册读事件
            socketChannel.register(selector,SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
