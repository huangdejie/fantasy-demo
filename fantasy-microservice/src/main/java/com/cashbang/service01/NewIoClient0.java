package com.cashbang.service01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Author: huangdj
 * @Date: 2020/6/11
 */
public class NewIoClient0 {

    public static void main(String[] args) {
        try {
            SocketChannel socketChannel = SocketChannel.open();
//            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("localhost",8080));
            //这一块并不意味着连接已经建立好了,所以需要判断是否就绪
            if(socketChannel.isConnectionPending()){
                socketChannel.finishConnect();
            }
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            byteBuffer.put("Hello,I'm SocketChannel Client".getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
            int i = socketChannel.read(byteBuffer);
            if(i>0){
                System.out.println("收到服务端的数据"+new String(byteBuffer.array()));
            }else{
                System.out.println("没有收到数据");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
