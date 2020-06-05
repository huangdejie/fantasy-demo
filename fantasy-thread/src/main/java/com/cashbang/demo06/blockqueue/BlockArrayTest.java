package com.cashbang.demo06.blockqueue;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class BlockArrayTest {

    public static void main(String[] args) throws InterruptedException {
        BlockArray blockArray = new BlockArray();
        Thread t1 = new Thread(()->{
            System.out.println("t1线程"+"开始获取");
            String value = blockArray.get();
            System.out.println("t1线程获取的接口为:"+value);
        },"t1");
        t1.start();
        Thread.sleep(1000);
        Thread t2 = new Thread(()->{
            System.out.println("t2线程增加数据");
            blockArray.add("t2_hello");
            System.out.println("t2线程增加数据成功");
        },"t2");
        t2.start();
        Thread t3 = new Thread(()->{
            System.out.println("t3线程增加数据");
            blockArray.add("t3_hi");
            System.out.println("t3线程增加数据成功");
        },"t3");
        t3.start();
        Thread.sleep(50000000);
    }

}
