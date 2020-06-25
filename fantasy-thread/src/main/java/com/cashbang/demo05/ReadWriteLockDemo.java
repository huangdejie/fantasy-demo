package com.cashbang.demo05;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class ReadWriteLockDemo<K,V> {

    final Map<K,V> m = new HashMap<K, V>();
    final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    final Lock r = rwl.readLock();
    final Lock w = rwl.writeLock();

    V get(K key){
        r.lock();
        try{
            return m.get(key);
        }finally {
            r.unlock();
        }
    }

    V put(K key,V value){
        w.lock();
        try{
            return m.put(key,value);
        }finally {
            w.unlock();
        }
    }
}
