package com.cashbang.demo04.threadlocal;

/**
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class ThreadLocalDemo {

    static ThreadLocal<Integer> local = new ThreadLocal<Integer>();

    public static void main(String[] args) {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(()->{
                int num = local.get();
                local.set(num+=5);
                System.out.println(Thread.currentThread().getName()+"_"+num);
            });

        }
        Thread thread = new Thread(()->{
           int num = local.get();
            local.set(num+=5);
            System.out.println(Thread.currentThread().getName()+"_"+num);
        });
        for (int i = 0; i < 5; i++) {
            threads[i].start();
        }
    }

}
