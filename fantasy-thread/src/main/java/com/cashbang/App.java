package com.cashbang;

/**
 * Hello world!
 *
 */
public class App {

    private static int count = 0;

    public static void incr(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        count++;
    }

    public static void main( String[] args ) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                App.incr();
            }).start();
        }
        Thread.sleep(3000);
        System.out.println("运行结果:"+count);
    }

}
