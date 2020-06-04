package com.cashbang.demo06;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/3
 */
public class ConditionDemo {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock(true);
        Condition condition = lock.newCondition();
        ConditionDemoWait waitDemo = new ConditionDemoWait(lock,condition);
        waitDemo.start();
        ConditionDemoSignal signalDemo = new ConditionDemoSignal(lock,condition);
        signalDemo.start();
    }

}
