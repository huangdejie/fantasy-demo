package com.cashbang.demo04.threadlocal;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huangdj
 * @Date: 2020/6/2
 */
public class SelfTest {

    public static void main(String[] args) {
        List<String> aa = new ArrayList<>();
        aa.add("hello");
        aa.add("hhhh");
        aa.add("nnnn");
        aa.add("sdfsaf");
        int i = 8;
        while (i<10){
            i++;
            System.out.println(i);
        }
        System.out.println("_______________________");
        int n = 1;
        System.out.println(n>>>=1);
    }


}
