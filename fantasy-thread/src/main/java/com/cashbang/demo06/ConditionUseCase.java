package com.cashbang.demo06;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/3
 */
public class ConditionUseCase {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void conditionWait(){
//        lock.lock();
        try{
            System.out.println(Thread.currentThread().getName()+"拿到锁了");
            System.out.println(Thread.currentThread().getName()+"等待信号");
            condition.await();
            System.out.println(Thread.currentThread().getName()+"拿到信号");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
//            lock.unlock();
        }
    }

    public void conditionSignal(){
//        lock.lock();
        try{
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName()+"拿到锁了");
            condition.signal();
            System.out.println(Thread.currentThread().getName()+"发出信号");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
//            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionUseCase useCase = new ConditionUseCase();
        Thread t1 = new Thread(()->{
            useCase.conditionWait();
        },"t1");
        t1.start();
        Thread t2 = new Thread(()->{
            useCase.conditionSignal();
        },"t2");
        t2.start();
    }

}
