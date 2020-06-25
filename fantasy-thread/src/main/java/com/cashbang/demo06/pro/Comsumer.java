package com.cashbang.demo06.pro;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: huangdj
 * @Date: 2020/6/4
 */
public class Comsumer implements Runnable {

    private Queue<String> msg;
    private int maxSize;
    Lock lock;
    Condition condition;

    public Comsumer(Queue<String> msg, int maxSize, Lock lock, Condition condition) {
        this.msg = msg;
        this.maxSize = maxSize;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        int i = 0;
        while (true){
            i++;
            lock.lock();
            while (msg.isEmpty()){
                System.out.println("消费者队列空了，先等待");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("消费消息："+msg.remove());
            condition.signal();
            lock.unlock();
        }
    }
}
