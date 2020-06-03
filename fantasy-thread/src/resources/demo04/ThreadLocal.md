### 死锁

两个线程在等待互相锁持有的锁。专业定义：一组互相竞争资源的线程因互相等待，导致永久阻塞的现象

造成死锁的四个必要条件：

1）互斥，即两个共享资源X和Y只能被一个线程占有

2）占有且等待，线程T1已经获得共享资源X，在等待共享资源Y的时候，不释放共享资源

3）不可抢占，其他线程不能强行抢占线程T1占有的资源

4）循环等待，T1线程等待T2占有的资源，T2等待T1占有的资源

解决死锁问题，只要破坏上述2、3、4三个条件中的一个就行了

### Thread.join()

让线程的执行结果对后续线程的访问可见

### ThreadLocal

1、每个线程内部都维护了一个ThrealdLocalMap

```java
//声明一个ThreadLocal变量local，此时开辟了一块内存，里面存放的local对象
ThradLocal<Integer> local = new ThreadLocal<Integer>();
//注意，这里不是赋值，赋值是=操作符
local.set(10);
```

此处的10是存放在local中吗？

1）10不是存放在local中，10和local是两个独立存放的东西，不是包含关系

2）10和local是两个独立存放的变量，如果其中的一个被清理，那么另外一个不受影响

10和local存放在哪呢？每个线程都维护了一个ThreadLocalMap(可以简单理解为map)，以key-value的形式存在，key就是local，10就是value。但是此时的key即local被包装成了一个弱引用，也就是外部的强引用不再被引用时，会被gc回收，一旦被回收，那此时就变成了null->10这这种情况，就无法找到10这个值，从而造成**内存泄漏**

**如何避免内存泄漏**：

​	调用ThreadLocal的get()、set()方法后再调用remove()方法，将Entry的节点和Map的引用关系移除，这样整个Entry对象再GC Roots分析后就编程不可达了，下次GC的时候就可以被回收。

ThreadLocal的核心机制：

1）每个线程内部维护了一个ThreadLocalMap

2）map中存储了线程本地对象key(ThreadLocal)和线程的变量副本value

3）Thread内的map由ThreadLocal维护，由ThreadLocal负责向Map获取和设置线程的变量值

即线程要想访问value必须通过ThreadLocal进行访问，不能直接访问value，ThreadLocal相当于一个中间人的角色。



Hash表相关知识：

​	理想状态下，hash函数可以将关键字均匀的分散到数组的不同位置，不会出现两个关键字散列值相同的情况。但在实际使用过程中，经常会出现多个关键字散列值相同的情况(被映射到数组的同一个位置)，这种情况即为**散列冲突**

​	解决散列冲突：

​	1）分离链表法：使用链表来解决冲突，将散列值相同的元素都保存到一个链表中。



![img](https://upload-images.jianshu.io/upload_images/2615789-32b422909f2f933c.gif?imageMogr2/auto-orient/strip|imageView2/2/w/696/format/webp)

​	2）开放地址法：此方法不会创建链表，当关键字散列到数组单元已经被另外一个关键字占用的时候，就会尝试在数组中寻找其他的单元，知道找到一个空的单元。  最简单的方法就是线性探测法——从冲突的数组单元开始，依次往后搜索空单元，如果到数组尾部，再从头开始搜索(环形查找)

![img](https://upload-images.jianshu.io/upload_images/2615789-0d85565e94c4bd6b.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/722/format/webp)

ThreadLocal中的数组位置计算为什么要通过位与运算？

​			位运算的执行效率远远高于了取模运算

#### 源码分析

```java
//key即为当前threadLocal，value即值
private void set(ThreadLocal<?> key, Object value) {

    // We don't use a fast path as with get() because it is at
    // least as common to use set() to create new entries as
    // it is to replace existing ones, in which case, a fast
    // path would fail more often than not.

    Entry[] tab = table;//获取entry[]数组
    int len = tab.length;//数组长度
    int i = key.threadLocalHashCode & (len-1);//得到索引i
	//从当前位置开始遍历直到遇到entry为null为止
    for (Entry e = tab[i];e != null;e = tab[i = nextIndex(i, len)]) {
        //得到entry中的key(即threadLocal)
        ThreadLocal<?> k = e.get();
        //如果entry中的key与当前key相等，则直接替换并返回
        if (k == key) {
            e.value = value;
            return;
        }
        //如果entry不为null但其key为null则表示此entry为脏entry替换
        if (k == null) {
            replaceStaleEntry(key, value, i);
            return;
        }
    }

    tab[i] = new Entry(key, value);
    int sz = ++size;
    if (!cleanSomeSlots(i, sz) && sz >= threshold)
        rehash();
}
```

**replaceStaleEntry(key, value, i);**

```java
//key为要操作的threadLocal,value为值，staleSlot为entry[]数组中脏entry的位置
private void replaceStaleEntry(ThreadLocal<?> key, Object value,
                                       int staleSlot) {
    Entry[] tab = table;
    int len = tab.length;
    Entry e;

    // Back up to check for prior stale entry in current run.
    // We clean out whole runs at a time to avoid continual
    // incremental rehashing due to garbage collector freeing
    // up refs in bunches (i.e., whenever the collector runs).
    //slotToExpunge为要清除的entry所在索引，初始为传过来的脏entry的位置
    int slotToExpunge = staleSlot;
    //1、从传过来的脏entry位置索引的前一个位置向前线性遍历entry[]数组，直到entry不为null为止
    for (int i = prevIndex(staleSlot, len);(e = tab[i]) != null;i = prevIndex(i, len))
        //如果遇到entry中的key为null则将slotToExpunge置为此entry所在位置
        if (e.get() == null)
            slotToExpunge = i;

    // Find either the key or trailing null slot of run, whichever
    // occurs first
    //从传过来的脏entry位置索引的下一个位置开始向后遍历数组知道entry为null为止
    for (int i = nextIndex(staleSlot, len);
         (e = tab[i]) != null;
         i = nextIndex(i, len)) {
        ThreadLocal<?> k = e.get();

        // If we find key, then we need to swap it
        // with the stale entry to maintain hash table order.
        // The newly stale slot, or any other stale slot
        // encountered above it, can then be sent to expungeStaleEntry
        // to remove or rehash all of the other entries in run.
       //如果当前entry的key与传入的key相等，将当前entry的value更新，交换table[i]和table[staleSlot]
        //此时table[staleSlot]就有了key——》valueNew，而table[i]为null——》valueOld
        if (k == key) {
            e.value = value;

            tab[i] = tab[staleSlot];
            tab[staleSlot] = e;

            // Start expunge at preceding stale entry if it exists
            //如果第1步中找到的位置和初始位置相同，则将slotToExpunge置为此时的位置i
            if (slotToExpunge == staleSlot)
                slotToExpunge = i;
            //清除脏entry
            cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
            return;
        }

        // If we didn't find stale entry on backward scan, the
        // first stale entry seen while scanning for key is the
        // first still present in the run.
        if (k == null && slotToExpunge == staleSlot)
            slotToExpunge = i;
    }

    // If key not found, put new entry in stale slot
    tab[staleSlot].value = null;
    tab[staleSlot] = new Entry(key, value);

    // If there are any other stale entries in run, expunge them
    if (slotToExpunge != staleSlot)
        cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
}
```

**expungeStaleEntry(int staleSlot)**

```java
//staleSlot脏entry所在的位置
private int expungeStaleEntry(int staleSlot) {
    Entry[] tab = table;
    int len = tab.length;

    // expunge entry at staleSlot
    //将value置为null，以及entry置为null，key无需置为null，因为已经是null了
    tab[staleSlot].value = null;
    tab[staleSlot] = null;
    size--;

    // Rehash until we encounter null
    Entry e;
    int i;
    //从当前脏entry的下一个位置开始线性遍历，直到entry为null
    for (i = nextIndex(staleSlot, len);
         (e = tab[i]) != null;
         i = nextIndex(i, len)) {
        ThreadLocal<?> k = e.get();
        //如果key为null则清空
        if (k == null) {
            e.value = null;
            tab[i] = null;
            size--;
        } else {
            //如果key不为null则根据threadLocal重新计算一次位置，说明是经过hash冲突
            int h = k.threadLocalHashCode & (len - 1);
            if (h != i) {
                tab[i] = null;

                // Unlike Knuth 6.4 Algorithm R, we must scan until
                // null because multiple entries could have been stale.
                //向下遍历找到找到为null的entry，然后将其替换为之前遍历的entry
                while (tab[h] != null)
                    h = nextIndex(h, len);
                tab[h] = e;
            }
        }
    }
    return i;
}
```

**cleanSomeSlots(int i, int n)**

```java
//i为脏entry开始向下遍历为null的entry所在的位置,n为数组长度
private boolean cleanSomeSlots(int i, int n) {
    boolean removed = false;
    Entry[] tab = table;
    int len = tab.length;
    do {
        i = nextIndex(i, len);
        Entry e = tab[i];
        if (e != null && e.get() == null) {
            n = len;
            removed = true;
            i = expungeStaleEntry(i);
        }
    } while ( (n >>>= 1) != 0);
    return removed;
}
```

