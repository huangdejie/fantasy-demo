package com.cashbang.demo06.pro;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/4
 */
public class Demo {

    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        int maxSize = 5;
        Producer producer = new Producer(queue,maxSize,lock,condition);
        Comsumer comsumer = new Comsumer(queue,maxSize,lock,condition);
        Thread t1 = new Thread(producer,"t1");
        Thread t2 = new Thread(comsumer,"t2");
        t1.start();
        t2.start();
    }

}
