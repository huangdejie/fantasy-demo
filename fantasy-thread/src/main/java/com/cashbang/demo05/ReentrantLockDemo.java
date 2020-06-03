package com.cashbang.demo05;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/2
 */
public class ReentrantLockDemo {

    private static Lock lock = new ReentrantLock(true);

    public static void main(String[] args) {
        int i = 0;
        lock.lock();
        try{
            i++;
        }finally {
            lock.unlock();
        }

    }

}
