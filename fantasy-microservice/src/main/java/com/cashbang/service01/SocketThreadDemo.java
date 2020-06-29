package com.cashbang.service01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: huangdj
 * @Date: 2020/6/11
 */
public class SocketThreadDemo {

    static ExecutorService executorService = Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println(socket.getPort());
                executorService.execute(new SocketThread(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
