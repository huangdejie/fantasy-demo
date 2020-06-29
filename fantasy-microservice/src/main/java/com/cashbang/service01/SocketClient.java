package com.cashbang.service01;

import java.io.*;
import java.net.Socket;

/**
 * @Author: huangdj
 * @Date: 2020/6/11
 */
public class SocketClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost",8080);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write("客户端1发送了一个消息\n");
        bufferedWriter.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String serverLine = bufferedReader.readLine();
        System.out.println("服务端返回的数据:"+serverLine);

    }

}
