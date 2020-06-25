package com.cashbang.demo06;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class CountDownLatchDemo {
    private static volatile AtomicInteger atomicInteger = new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0;i<5;i++){
            new Thread(()->{
                atomicInteger.addAndGet(1);
                countDownLatch.countDown();
            }).start();
        }
        new Thread(()->{
            try {
                countDownLatch.await();
                System.out.println("线程获取值:"+atomicInteger.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        countDownLatch.await();
        System.out.println(atomicInteger.get());
    }

}
