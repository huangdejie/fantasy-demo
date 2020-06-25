package com.cashbang.demo06.processon;

/**
 * 风骚的Mic 老师
 * create-date: 2020/5/27-22:09
 */
public interface RequestProcessor {

    void processRequest(Request request);

    void shutdown();
}
