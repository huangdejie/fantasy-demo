package com.cashbang.demo05;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/3
 */
public class ReentrantReadWriteLockDemo {

    static Map<String,String> cacheMap = new HashMap<>();
    static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static Lock read = rwl.readLock();
    static Lock write = rwl.writeLock();

    public static String get(String key){
        read.lock();
        try{
            if(Thread.currentThread().getName().equals("t1")){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return cacheMap.get(key);
        }finally {
            read.unlock();
        }
    }

    public static Object write(String key,String value){
        write.lock();
        try {
            if(Thread.currentThread().getName().equals("t3")){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cacheMap.put(key, value);
            return value;
        }finally {
            write.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        cacheMap.put("hello","world");
        cacheMap.put("nihao","ooo");
        Thread t1 = new Thread(()->{
            System.out.println("Read_Thread:"+Thread.currentThread().getName()+"获取数据"+get("nihao"));
        },"t1");
        t1.start();
        Thread.sleep(1000);
        Thread t2 = new Thread(()->{
            System.out.println("Read_Thread:"+Thread.currentThread().getName()+"获取数据"+get("hello"));
        },"t2");
        t2.start();
        t1.join();
        t2.join();
        System.out.println("************分割线**************");
        Thread t3 = new Thread(()->{
            System.out.println("Write_Thread:"+Thread.currentThread().getName()+"插入数据"+write("python","haha"));
        },"t3");
        t3.start();
        Thread.sleep(1000);
        Thread t4 = new Thread(()->{
            System.out.println("Write_Thread:"+Thread.currentThread().getName()+"插入数据"+write("java","JuC"));
        },"t4");
        t4.start();
        t3.join();
        t4.join();
        System.out.println(cacheMap.get("java")+"--------"+cacheMap.get("python"));
    }


}
