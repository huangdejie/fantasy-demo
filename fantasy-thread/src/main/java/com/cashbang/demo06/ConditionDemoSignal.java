package com.cashbang.demo06;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: huangdj
 * @Date: 2020/6/3
 */
public class ConditionDemoSignal extends Thread{

    private Lock lock;
    private Condition condition;

    public ConditionDemoSignal(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        System.out.println("begin - conditionDemoSignal");
        lock.lock();
        try{
            condition.signal();
//            Thread.sleep(5000);
            System.out.println("end - conditionDemoSignal");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
