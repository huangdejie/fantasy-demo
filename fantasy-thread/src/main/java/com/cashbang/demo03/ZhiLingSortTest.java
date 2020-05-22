package com.cashbang.demo03;

/**
 * @Author: huangdj
 * @Date: 2020/5/21
 */
public class ZhiLingSortTest {

    private String _23;
    private static int x =0,y=0;
    private static int a=0,b=0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for(;;){
            i++;
            x=0;y=0;
            a=0;b=0;
            Thread one = new Thread(()->{
                shortWait(100000);
                a=1;
                x=b;
            });
            Thread other = new Thread(()->{
                b=1;
                y=a;
            });
            one.start();
            other.start();
            one.join();
            other.join();
            String result = ""+i+"次("+x+","+y+")";
            if(x==0 && y==0){
                System.out.println(result);
                break;
            }else{
                System.out.println(result);
            }
        }
    }

    private static void shortWait(long interval) {
        long start = System.nanoTime();
        long end;
        do{
            end = System.nanoTime();
        }while (start+interval >= end);
    }

}
