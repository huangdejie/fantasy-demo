package com.cashbang.demo06;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/4
 */
public class AqsConditionTest {

    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(()->{
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        },"t1");
        t1.start();
        Thread.sleep(1000);
        Thread t2 = new Thread(()->{
            lock.lock();
            try {
                condition.await();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        },"t2");
        t2.start();
        Thread.sleep(1000);
        Thread t3 = new Thread(()->{
            lock.lock();
            try {
                condition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }

        },"t3");
        t3.start();
    }

}
