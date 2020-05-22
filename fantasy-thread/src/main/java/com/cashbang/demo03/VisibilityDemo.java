package com.cashbang.demo03;

/**
 * @Author: huangdj
 * @Date: 2020/5/21
 */
public class VisibilityDemo {

    static boolean stop = false;
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(()->{
            int i = 0;
            while (!stop){
                i++;
                System.out.println("rs:"+i);
//                try {
//                    Thread.sleep(0);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
        thread.start();
        Thread.sleep(1000);
        stop=true;
    }


}
