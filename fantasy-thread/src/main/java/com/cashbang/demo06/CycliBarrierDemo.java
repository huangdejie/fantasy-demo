package com.cashbang.demo06;

import java.util.concurrent.CyclicBarrier;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class CycliBarrierDemo extends Thread{

    @Override
    public void run() {
        System.out.println("开始进行数据分析");
    }

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3,new CycliBarrierDemo());
        new Thread(new CyclicBarrierDemo01(cyclicBarrier,"file1")).start();
        new Thread(new CyclicBarrierDemo01(cyclicBarrier,"file2")).start();
        new Thread(new CyclicBarrierDemo01(cyclicBarrier,"file3")).start();
    }
}
