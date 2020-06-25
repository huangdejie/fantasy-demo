package com.cashbang.demo06.processon;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:08
 */
public class Request {

    private String name;

    public Request(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Request{" +
                "name='" + name + '\'' +
                '}';
    }
}
