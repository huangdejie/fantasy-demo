package com.cashbang.demo04.threadlocal;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class ThreadLocalIndexDemo {

    private static AtomicInteger nextHashCode = new AtomicInteger();

    private static final int HASH_INCREMENT = 0x61c88647;

    private static int nextHashCode() {
        return nextHashCode.getAndAdd(HASH_INCREMENT);
    }

    private static int threadLocalHashCode = nextHashCode();

    private static final int INITIAL_CAPACITY = 16;


    public static void main(String[] args) {
        for(int j = 0;j<16;j++) {
            int i = threadLocalHashCode & (INITIAL_CAPACITY - 1);
            System.out.println(i);
        }
    }

}
