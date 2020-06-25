package com.cashbang.demo06.processon;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:11
 */
public class FinalRequestProcessor extends Thread implements RequestProcessor{
    //存储请求数据
    BlockingQueue<Request> requests=new LinkedBlockingDeque<>();

    volatile boolean finished=false;


    @Override
    public void run() {
        while(!finished||!Thread.currentThread().isInterrupted()){
            try {
                Request request=requests.take();
                System.out.println("Final Processor:"+request);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void processRequest(Request request) {
        requests.add(request);
    }

    @Override
    public void shutdown() {
        System.out.println("Finish Shutdown ");
        finished=true;
        requests.clear();
    }
}
