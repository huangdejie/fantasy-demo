package com.cashbang.demo05;

import java.util.concurrent.locks.StampedLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class StampedLockDemo {


    public static void main(String[] args) {
        final StampedLock sl = new StampedLock();
        long stamp = sl.readLock();
        try{
            //....
        }finally {
            sl.unlockRead(stamp);
        }

        long writeStamp = sl.writeLock();
        try{
            //.....
        }finally {
            sl.unlockWrite(writeStamp);
        }
    }


}
