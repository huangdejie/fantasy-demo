package com.cashbang.demo02;

import org.openjdk.jol.info.ClassLayout;

/**
 * @Author: huangdj
 * @Date: 2020/5/20
 */
public class SynchronizedDemo {
    public static void main(String[] args) {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
//        synchronized (synchronizedDemo){
//
//            System.out.println("locking");
//            System.out.println(ClassLayout.parseInstance(synchronizedDemo).toPrintable());
//        }

        new Thread(()->{
            synchronized (synchronizedDemo){
                System.out.println("t1---locking");
                System.out.println(ClassLayout.parseInstance(synchronizedDemo).toPrintable());
                System.out.println("***************************");

            }
        },"t1").start();
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        new Thread(()->{
            synchronized (synchronizedDemo){
//
                System.out.println("t2---locking");
                System.out.println(ClassLayout.parseInstance(synchronizedDemo).toPrintable());
                System.out.println("***************************");

            }
        },"t2").start();
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
