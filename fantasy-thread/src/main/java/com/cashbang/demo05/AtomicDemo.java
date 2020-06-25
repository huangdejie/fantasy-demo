package com.cashbang.demo05;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AtomicDemo {

    private static int count = 0;

    static Lock lock = new ReentrantLock(false);

    public static void inc(){
        lock.lock();
        try {
            Thread.sleep(1);
            count++;
            decr();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    private static void decr() {
        lock.lock();
        try{
        count--;
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            AtomicDemo.inc();
        });

        Thread t2 = new Thread(()->{
            AtomicDemo.inc();
        });
        t1.start();
        t2.start();
        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                AtomicDemo.inc();
            }).start();
        }
        Thread.sleep(50000);
        System.out.println("result:"+count);
    }

}
