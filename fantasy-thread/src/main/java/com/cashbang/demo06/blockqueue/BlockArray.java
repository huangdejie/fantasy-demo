package com.cashbang.demo06.blockqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/4
 */
public class BlockArray {

    private Lock lock = new ReentrantLock();

    private List<String> queue = new ArrayList<>();

    private Condition notFull = lock.newCondition();

    private Condition notEmpty = lock.newCondition();
    private int maxSize = 5;

    public boolean add(String value){
        lock.lock();
        try{
            if(queue.size() == maxSize){
                System.out.println("队列已满线程:"+Thread.currentThread().getName()+"执行add()方法阻塞");
                notFull.await();
            }
            System.out.println("线程:"+Thread.currentThread().getName()+"开始执行add()");
            queue.add(value);
            System.out.println("线程:"+Thread.currentThread().getName()+"执行add()结束");
            notEmpty.signal();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return false;
    }

    public String get(){
        lock.lock();
        try{
            if(queue.size()==0){
                System.out.println("队列为空，线程:"+Thread.currentThread().getName()+"执行get()方法阻塞");
                notEmpty.await();
            }
            System.out.println("线程:"+Thread.currentThread().getName()+"开始执行get()");
            String value = queue.get(0);
            System.out.println("线程:"+Thread.currentThread().getName()+"执行get()结束");
            notFull.signal();
            return value;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }
}
