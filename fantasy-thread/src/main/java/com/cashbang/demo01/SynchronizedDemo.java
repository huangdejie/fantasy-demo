package com.cashbang.demo01;

/**
 * @Author: huangdj
 * @Date: 2020/5/19
 */
public class SynchronizedDemo {

    static Integer count = 0;

    public static void incr(){
        synchronized (count){
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }
    }

    /**
     * count为1000以内的随机数，因为synchronized锁住的是一个对象，但count在执行++操作时会生成一个新的对象，
     * 所以多个线程在进行锁对象的时候锁住的不是一个新的对象，所以就无法达到锁住同一个资源的效果
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(()->SynchronizedDemo.incr()).start();
        }
        Thread.sleep(5000);
        System.out.println("result:"+count);

//        Integer integer = 1;
//        integer++;
//        System.out.println();
    }

}
