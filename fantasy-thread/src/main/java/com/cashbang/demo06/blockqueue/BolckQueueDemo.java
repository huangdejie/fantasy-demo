package com.cashbang.demo06.blockqueue;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: huangdj
 * @Date: 2020/6/4
 */
public class BolckQueueDemo {

    static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"开始执行取数据");
            try {
                queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"t1");
        Object a = queue.remove();
        System.out.println(a.toString());
    }

}
