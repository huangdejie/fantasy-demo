package com.cashbang.demo06;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: huangdj
 * @Date: 2020/6/3
 */
public class ConditionDemoWait extends Thread{


    private Lock lock;
    private Condition condition;

    public ConditionDemoWait(Lock lock,Condition condition){
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
        System.out.println("begin - conditionDemoWait");
        lock.lock();
        try{
            condition.await();
            System.out.println("end - conditionDemoWait");
        }catch (InterruptedException exception){
            exception.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
