package com.cashbang.demo06;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class CyclicBarrierDemo01 extends Thread {

    private CyclicBarrier cyclicBarrier;

    private String path;

    public CyclicBarrierDemo01(CyclicBarrier cyclicBarrier, String path) {
        this.cyclicBarrier = cyclicBarrier;
        this.path = path;
    }

    @Override
    public void run() {
        System.out.println("开始导入:"+path+"位置的数据");
        try {
            cyclicBarrier.await();//阻塞
            System.out.println("结束导入:"+path+"位置的数据");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
