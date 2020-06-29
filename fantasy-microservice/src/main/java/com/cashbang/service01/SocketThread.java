package com.cashbang.service01;

import java.io.*;
import java.net.Socket;

/**
 * @Author: huangdj
 * @Date: 2020/6/11
 */
public class SocketThread implements Runnable {


    private Socket socket;
    public SocketThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s = bufferedReader.readLine();//阻塞
            String clientStr = s;
            Thread.sleep(5000000);
            System.out.println("接收到客户端的消息:"+clientStr);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("服务端收到了消息\n");
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
