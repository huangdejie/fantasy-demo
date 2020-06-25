### ReentrantLock

#### lock();

调用acquire(1)

```
public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
}
```

tryAcquire(arg)

```java
//以独占方式获取资源，成功返回true，失败返回false
protected final boolean tryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    //获取锁的状态
    int c = getState();
    //
    if (c == 0) {
        //与非公平锁不同的是此处有这行代码（即判断同步队列中当前节点是否有前驱节点）。如果该方法返回true，
        //表示有线程比当前线程更早的请求获取锁，因此需要等待前驱线程获取并释放锁之后才能继续获取锁。
        //而非公平锁则无需判断，直接通过cas抢占锁资源
        if (!hasQueuedPredecessors() &&
            //cas替换
            compareAndSetState(0, acquires)) {
            //设置独占exclusiveOwnerThread
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
}
```



```java
private Node addWaiter(Node mode) {
    //以给定模式构造节点。mode有两种，EXCLUSIVE(独占)和SHATRED(共享)
    Node node = new Node(Thread.currentThread(), mode);
    // Try the fast path of enq; backup to full enq on failure
    Node pred = tail;
    //尝试快速方式直接放到队尾
    if (pred != null) {
        node.prev = pred;
        if (compareAndSetTail(pred, node)) {
            pred.next = node;
            return node;
        }
    }
    //通过enq入队
    enq(node);
    return node;
}
```

通过tryAcquire()失败和addWaiter()线程获取资源失败已经被放入等待队列尾部了。

acquireQueued(final Node node, int arg)；

```java

final boolean acquireQueued(final Node node, int arg) {
    //标记是否成功拿到资源
    boolean failed = true;
    try {
        //标记等待过程中是否被中断过
        boolean interrupted = false;
        for (;;) {
            //拿到当前节点的前驱节点
            final Node p = node.predecessor();
            //如果前驱节点为头节点就尝试获取锁
            if (p == head && tryAcquire(arg)) {
                //线程获取锁成功，将头节点指向当前节点，并且将当前节点中的thread置为null，
                //前驱节点置为null
                setHead(node);
                //p节点的下一个节点置为null，此时这个节点等待GC回收
                p.next = null; // help GC
                //获取锁成功标识
                failed = false;
                return interrupted;
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                interrupted = true;
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

shouldParkAfterFailedAcquire(Node pred, Node node);

waitStatus节点状态：表示当前Node节点的等待状态

1、CANCELLED(1):表示当前节点已取消调度，当timeout或被中断(响应中断的情况下)，会触发变更为此状态。进		入该状态后节点将不会再有变化

2、SINGAL(-1):表示后继节点在等待当前节点唤醒。后继节点入队时，会将前继节点的状态更新为SINGAL

3、CONDITION(-2):表示节点等待在Condition上，当其他线程调用了Condition的signal()方法后，CONDITION状		态的节点从等待队列转移到同步队列中，等待获取同步锁

4、PROPAGATE(-3):共享模式下，前继节点不仅会唤醒其后继节点，同时也可能会唤醒后继的后继节点

5、0：新节点入队时的默认状态

***注意：***负值表示节点处于有效等待状态，而正值表示节点已被取消。所以源码中很多地方用>0、<0来判断节点的状态是否正常。



获取锁失败之后

```java
//此方法主要用于检查状态，看看自己是否真的可以去休息了，即进入waiting状态
//这个方法保证前驱节点的状态必须为SINGAL，否则自己就不能安心去休息
private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
    int ws = pred.waitStatus;
    if (ws == Node.SIGNAL)
        /*
             * This node has already set status asking a release
             * to signal it, so it can safely park.
             */
        return true;
    if (ws > 0) {//ws>)，意味着prev节点取消了排队，直接移除这个节点就行
        /*
             * Predecessor was cancelled. Skip over predecessors and
             * indicate retry.
             */
        do {
            node.prev = pred = pred.prev;//相当于pred=pred.prev;node.prev=pred;
        } while (pred.waitStatus > 0);
        pred.next = node;
    } else {
        /*
             * waitStatus must be 0 or PROPAGATE.  Indicate that we
             * need a signal, but don't park yet.  Caller will need to
             * retry to make sure it cannot acquire before parking.
             */
        //将前驱节点的waitStatus设置为-1
        compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
    }
    return false;
}
```

将前继节点的waitStatus设置为-1后，调用parkAndCheckInterrupt()让当前线程进入waiting状态。在此状态下，有两种方式可以唤醒该线程：1）被unpark()；2）被interrupt()

```jav
private final boolean parkAndCheckInterrupt() {
		//park()会使当前线程进入waiting状态
        LockSupport.park(this);
        return Thread.interrupted();//返回当前线程是否被其他线程触发过中断请求，也就是
        //thread.interrupt();如果有触发过中断请求，那么这个方法会返回当前的中断标识true，并且对中断
   //标识进行复位标识已经响应了中断请求。如果返回true，意味着在acquire()方法中会执行selfInterrupt()
}
```



***********************************************************************************************************************************************************************************************************************************************************************

#### unlock()

```java
public final boolean release(int arg) {
    //完全释放锁成功后
    if (tryRelease(arg)) {
        //获取头节点
        Node h = head;
        //头节点不为null并且waitStatus不为0
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

```java
//将线程状态state减1，如果state为0则表示释放锁成功，若不为0可能表示重入，只有为0时才表示当前线程已完全释
//放锁，并将独占线程置为null
protected final boolean tryRelease(int releases) {
    int c = getState() - releases;
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    boolean free = false;
    if (c == 0) {
        free = true;
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}
```

unparkSuccessor(Node node)

```java
//node为head节点
private void unparkSuccessor(Node node) {
    /*
         * If status is negative (i.e., possibly needing signal) try
         * to clear in anticipation of signalling.  It is OK if this
         * fails or if status is changed by waiting thread.
         */
    int ws = node.waitStatus;
    if (ws < 0)
        //将头节点置为0
        compareAndSetWaitStatus(node, ws, 0);

    /*
         * Thread to unpark is held in successor, which is normally
         * just the next node.  But if cancelled or apparently null,
         * traverse backwards from tail to find the actual
         * non-cancelled successor.
         */
    //头节点的后继节点
    Node s = node.next;
    if (s == null || s.waitStatus > 0) {//如果为null或者已取消
        s = null;
        //从后往前找直到找不到为止，将最前面waitStatus<0的节点拿出来
        for (Node t = tail; t != null && t != node; t = t.prev)
            if (t.waitStatus <= 0)
                s = t;
    }
    if (s != null)//next节点不为空，直接唤醒这个线程即可
        LockSupport.unpark(s.thread);
}
```

### AQS内部实现

AQS队列内部维护的是一个FIFO的双向链表，其特点是每个数据结构都有两个指针，分别指向直接后继节点和直接前继节点。所以双向链表可以从任意一个节点开始很方便的方位前继和后继节点。每个Node封装了线程，当线程争抢锁失败后会封装成Node加入到AQS队列中去；当获取锁的线程释放锁以后，会从队列中唤醒一个阻塞的节点(线程)

### LockSupport

提供了基本的线程同步原语。实际上调用的是UnSafe类里的函数

```java
public native void park(boolean var1, long var2);
public native void unpark(Object var1);
```

unPark()函数为线程提供""许可(permit)",线程调用park函数则等待“许可”。permit相当于0/1的开关，默认为0，调用一次unpark就加1变成了1。调用一次park会消费permit，又会变成0。如果在调用park()会阻塞，因为permit已经是0了。直到permit变成1.这时调用unpark会把permit设置为1.每个线程都有一个相关的permit，permit最多只有一个，重复调用unpark不会累积





-----------------------------------------------------------------------------------------------------------------------------------------------------------

### 面试相关：

1、AQS中的tryAcquire()方法为什么不定义成abstract？

因为锁分为独占锁和共享锁两种，在独占模式下只用实现tryAcquire-tryRelease，而共享模式下只用实现tryAcquireShared-tryReleaseShared。如果都定义成abstract，那么每个模式也要去实现另一个模式下的接口，这样就冗余了

2、为什么在释放锁的时候是从tail进行扫描的？

```java
private Node enq(final Node node) {
    for (;;) {
        Node t = tail;
        if (t == null) { // Must initialize
            if (compareAndSetHead(new Node()))
                tail = head;
        } else {
            //1、将新的节点的prev指向tail
            node.prev = t;
            //2、通过cas将tail设置为新的节点，因为cas是原子操作所以能够保证线程安全性
            if (compareAndSetTail(t, node)) {
                //3、设置原tail的next节点指向新的节点
                t.next = node;
                return t;
            }
        }
    }
}
```

在cas操作之后，t.next=node操作之前。存在其他线程调用unlock方法从head开始往后遍历，由于t.next=node还没有执行意味着链表的关系还没有建立完整。就会导致遍历到t节点的时候被中断。所以从后往前遍历，一定不会存在这个问题。

