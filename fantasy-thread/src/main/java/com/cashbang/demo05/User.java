package com.cashbang.demo05;

import java.util.concurrent.locks.LockSupport;

/**
 * @Author: huangdj
 * @Date: 2020/6/2
 */
public class User {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        LockSupport.park();
    }
}
