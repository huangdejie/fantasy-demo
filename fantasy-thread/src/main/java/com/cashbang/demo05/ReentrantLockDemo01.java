package com.cashbang.demo05;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/2
 */
public class ReentrantLockDemo01 {

    private static Lock lock = new ReentrantLock();
    static int count = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()->{
            lock.lock();
            try{
                count++;
                Thread.sleep(200000000);
            }catch (InterruptedException ex){
                ex.printStackTrace();
            }finally {
                lock.unlock();
            }
        },"t1");
        thread.start();
        Thread.sleep(1000);
        Thread t2 = new Thread(()->{
            lock.lock();
            try{
                count++;
            }finally {
                lock.unlock();
            }
        },"t2");
        t2.start();
        Thread.sleep(50000000);
        Thread t3 = new Thread(()->{
            lock.lock();
            try{
                count++;
            }finally {
                lock.unlock();
            }
        },"t3");

        t3.start();
    }

}
