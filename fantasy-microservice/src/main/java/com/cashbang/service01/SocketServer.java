package com.cashbang.service01;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: huangdj
 * @Date: 2020/6/10
 */
public class SocketServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Socket socket = serverSocket.accept();
        System.out.println(socket.getPort());
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Thread.sleep(50000000);
        bufferedWriter.write("服务端返回数据\n");
        bufferedWriter.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String strLine = bufferedReader.readLine();
        System.out.println("客户端请求数据"+strLine);
    }

}
