package com.cashbang.demo03;

/**
 * @Author: huangdj
 * @Date: 2020/5/21
 */
public class VolatileDemo {

    int i = 0;
    volatile boolean v =false;

    public void writer(){
        i = 42;
        v=true;
    }
    public void reader(){
        if (v == true){
            System.out.println(i);
        }
    }


}
