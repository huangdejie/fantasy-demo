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



