package com.cashbang.demo05;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class Cache<K,V> {

    final Map<K,V> m = new HashMap<K, V>();
    final ReadWriteLock rwl = new ReentrantReadWriteLock();
    final Lock r = rwl.readLock();
    final Lock w = rwl.writeLock();

    V get(K key){
        V v = null;
        r.lock();
        try {
            v = m.get(key);
        }finally {
            r.unlock();
        }
        //如果缓存中不存在，直接返回
        if(v != null){
            return v;
        }
        w.lock();
        try {
            v = m.get(key);
            if (v == null) {
                // TODO: 2020/6/5 查询数据库
                m.put(key, v);
            }
        }finally {
            w.unlock();
        }
        return v;
    }

}
