package com.cashbang.demo05;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/3
 */
public class ReentrantReadWriteLockDemo {

    static Map<String,Object> cacheMap = new HashMap<>();
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock read = rwl.readLock();
    static Lock write = rwl.writeLock();

    public static Object get(String key){
        read.lock();
        try{

            return cacheMap.get(key);
        }finally {
            read.unlock();
        }
    }

    public static Object write(String key,Object value){
        write.lock();
        try {
            return cacheMap.put(key, value);
        }finally {
            write.unlock();
        }
    }

    public static void main(String[] args) {
        cacheMap.put("hello","world");
        cacheMap.put("nihao","ooo");
        Thread t1 = new Thread(()->{

        },"t1");
        t1.start();
    }


}
