package com.cashbang.demo01;

import java.util.concurrent.TimeUnit;

/**
 * @Author: huangdj
 * @Date: 2020/5/19
 */
public class InterruptTest extends Thread{

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            try {
                System.out.println("emm...");
                TimeUnit.SECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName()+"have done");
    }

    /**
     * interrupt()发出一个信号来终止线程，但线程不一定马上停止
     * 若线程处于waiting状态，则无法响应此终止信号，那么就需要抛出interruptedException异常来决定如何应对
     * 此次终止信号。操作系统底层维护了一个interrupted变量，初始值为false，interrupt()方法会将该值置为true，
     * 在抛出interruptedException异常后会将此变量复位重置为false
     *
     * interrupt()的作用
     *  1、设置一个共享变量的值true
     *  2、唤醒处于阻塞状态下的线程
     * @param args
     */
    public static void main(String[] args) {
        InterruptTest interruptTest = new InterruptTest();
        interruptTest.setName("interrupt-thread");
        interruptTest.start();
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        interruptTest.interrupt();
        System.out.println("Main thread finished...");
    }
}
