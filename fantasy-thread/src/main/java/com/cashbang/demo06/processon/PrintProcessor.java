package com.cashbang.demo06.processon;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:10
 * 保存数据
 */
public class PrintProcessor extends Thread implements RequestProcessor{

    RequestProcessor nextProcessor;

    //存储请求数据
    BlockingQueue<Request> requests=new LinkedBlockingDeque<>();

    volatile boolean finished=false;

    public PrintProcessor(RequestProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    @Override
    public void run() {
        while(!finished||!Thread.currentThread().isInterrupted()){
            try {
                Request request=requests.take(); //阻塞式的获取请求
                System.out.println("Print:"+request);
                nextProcessor.processRequest(request); //传递给下一个处理器
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void processRequest(Request request) {
        requests.add(request); //生产消息
    }

    @Override
    public void shutdown() {
        //
        System.out.println("PrintProcessor begin shutdown");
        finished=true;
        requests.clear();
        nextProcessor.shutdown();
    }
}
