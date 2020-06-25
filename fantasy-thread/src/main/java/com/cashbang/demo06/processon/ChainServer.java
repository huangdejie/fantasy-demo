package com.cashbang.demo06.processon;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:19
 */
public class ChainServer {

    private RequestProcessor firstProcessor;

    private void setupRequestProcessor(){
        //构建一个链路
        //Print->Save->Final
        RequestProcessor finalProcessor=new FinalRequestProcessor();
        RequestProcessor saveProcessor=new SaveProcessor(finalProcessor);
        firstProcessor=new PrintProcessor(saveProcessor);
        //分别启动三个线程
        ((FinalRequestProcessor)finalProcessor).start();
        ((SaveProcessor)saveProcessor).start();
        ((PrintProcessor)firstProcessor).start();
    }
    public void startup(){
        setupRequestProcessor();
    }
    public void shutdown(){
        firstProcessor.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        ChainServer chainServer=new ChainServer();
        chainServer.startup();

        Request request=new Request("Mic");
        chainServer.firstProcessor.processRequest(request);

        Thread.sleep(5000);

        chainServer.shutdown();
    }
}
