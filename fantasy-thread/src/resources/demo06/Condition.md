### Condition

await()/signal()。调用await()方法会释放锁并等待，而其他线程调用condition的signal()方法通知并唤醒被阻塞的线程，然后自己执行unlock释放锁，被唤醒的线程获得之前的锁继续执行，最后释放锁。

**注意：**对于await/signal的调用必须是在本线程获取了独占锁的前提下，如果未获取到锁，则会抛出java.lang.IllegalMonitorStateException

#### await()

```java
public final void await() throws InterruptedException {
    if (Thread.interrupted())//表示await允许被中断
        throw new InterruptedException();
    Node node = addConditionWaiter();//创建一个新的节点，节点状态未condition
    int savedState = fullyRelease(node);//因为调用await()方法的前提是获取到锁。这里释放当前的锁，得		//到锁的状态，并唤醒AQS队列中的一个线程
    
    
    int interruptMode = 0;
    //判断当前节点是否在同步队列中，返回false表示不在，返回true表示在
    while (!isOnSyncQueue(node)) {
        //在这阻塞由AQS中的unLock进行唤醒
        LockSupport.park(this);
        if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
            break;
    }
    //当这个线程醒来，会尝试获取锁，当acquireQueued()返回false就是拿到锁了，失败就会在acquireQueued中			等待
    //interruptMode != THROW_IE
    if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
        interruptMode = REINTERRUPT;
    //如果node的下一个等待者不是null，则进行清理Condition队列上的节点(正常情况下nextWaiter为null，当线程被取消了，nextWaiter就不为null了，此时需要进行清理)
    if (node.nextWaiter != null) // clean up if cancelled
        unlinkCancelledWaiters();
    if (interruptMode != 0)
        reportInterruptAfterWait(interruptMode);
}
```



fullyRelease(Node node)

```java
//彻底的释放锁，就是如果当前锁存在多次重入，那么在这个方法中只需要释放一次就会把所有的重入次数归零
final int fullyRelease(Node node) {
    boolean failed = true;
    try {
        int savedState = getState();//获得state值
        if (release(savedState)) {//释放锁并且唤醒下一个同步队列中的线程，直接将state-savedState此时锁为释放状态0
            
            failed = false;
            return savedState;
        } else {
            throw new IllegalMonitorStateException();
        }
    } finally {
        if (failed)
            node.waitStatus = Node.CANCELLED;
    }
}
```

release(savedState)

```java
public final boolean release(int arg) {
    if (tryRelease(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            //唤醒同步队列中头节点的后继节点中的线程
            unparkSuccessor(h);
        return true;
    }
    return false;
}
```

acquireQueued(final Node node, int arg)

```java
//此时这个节点在AQS队列中，arg为
final boolean acquireQueued(final Node node, int arg) {
    boolean failed = true;
    try {
        boolean interrupted = false;
        for (;;) {
            final Node p = node.predecessor();
            if (p == head && tryAcquire(arg)) {
                setHead(node);
                p.next = null; // help GC
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

-----------------------------------------------------------------------------------------------------------------------------------------------------------

#### signal()

```java
public final void signal() {
    if (!isHeldExclusively())//判断当前线程是否获取到了锁，直接用当前线程与获得锁的线程相比即可
        throw new IllegalMonitorStateException();
    Node first = firstWaiter;//拿到condition队列上的第一个节点
    if (first != null)
        doSignal(first);
}
```

doSignal()

```java
private void doSignal(Node first) {
    do {
        //从Condition队列中删除first节点
        if ( (firstWaiter = first.nextWaiter) == null)
            lastWaiter = null;
        first.nextWaiter = null;
    } while (!transferForSignal(first) &&
             (first = firstWaiter) != null);
}
```

transferForSignal()

```java
final boolean transferForSignal(Node node) {
    /*
     * If cannot change waitStatus, the node has been cancelled.
     */
    if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
    //更新节点的状态为0，如果更新失败，只有一种情况就是节点被CANCELLED了
        return false;

    /*
     * Splice onto queue and try to set waitStatus of predecessor to
     * indicate that thread is (probably) waiting. If cancelled or
     * attempt to set waitStatus fails, wake up to resync (in which
     * case the waitStatus can be transiently and harmlessly wrong).
     */
    Node p = enq(node);//将当前节点添加到AQS队列中，并且返回node节点的上一个节点，即原tail节点
    int ws = p.waitStatus;
    if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
        LockSupport.unpark(node.thread);
    return true;
}
```



### CountDownLatch

计数器，通过设置state的值，调用await()方法会等待state的值减为0。其实际是共享锁

await()

```java
public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
    if (Thread.interrupted())
        throw new InterruptedException();
    if (tryAcquireShared(arg) < 0)//尝试获取资源，其实是判断state的值是否为0，为0返回1表示获得资源，否则返回-1表示未获取到资源，进入到下面这个方法。在AQS中，其返回值为负值表示获取失败；0表示获取成功，但没有剩余资源；正数表示获取成功，还有剩余资源，其他线程可以去获取
        doAcquireSharedInterruptibly(arg);
}
```

doAcquireSharedInterruptibly(arg);

```java
private void doAcquireSharedInterruptibly(int arg)
    throws InterruptedException {
    //添加到aqs队列
    final Node node = addWaiter(Node.SHARED);
    boolean failed = true;
    try {
        for (;;) {
            final Node p = node.predecessor();
            if (p == head) {
                //判断是否拿到锁
                int r = tryAcquireShared(arg);
                if (r >= 0) {//1
                    setHeadAndPropagate(node, r);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
            }
            if (shouldParkAfterFailedAcquire(p, node) &&
                parkAndCheckInterrupt())
                throw new InterruptedException();
        }
    } finally {
        if (failed)
            cancelAcquire(node);
    }
}
```

```java
private void setHeadAndPropagate(Node node, int propagate) {
    Node h = head; // Record old head for check below
    //将当前节点设置成头节点
    setHead(node);
    /*
     * Try to signal next queued node if:
     *   Propagation was indicated by caller,
     *     or was recorded (as h.waitStatus either before
     *     or after setHead) by a previous operation
     *     (note: this uses sign-check of waitStatus because
     *      PROPAGATE status may transition to SIGNAL.)
     * and
     *   The next node is waiting in shared mode,
     *     or we don't know, because it appears null
     *
     * The conservatism in both of these checks may cause
     * unnecessary wake-ups, but only when there are multiple
     * racing acquires/releases, so most need signals now or soon
     * anyway.
     */
    if (propagate > 0 || h == null || h.waitStatus < 0 ||
        (h = head) == null || h.waitStatus < 0) {
        Node s = node.next;
        if (s == null || s.isShared())
            //唤醒后继节点的线程
            doReleaseShared();
    }
}
```



-----------------------------------------------------------------------------------------------------------------------------------------------------------

### BlockingQueue(FIFO)

#### ArrayBlockingQueue

##### 添加方法

| 添加方法                             |                                                          |
| ------------------------------------ | -------------------------------------------------------- |
| add(value)                           | 队列满了的时候会抛出异常                                 |
| offer(value)                         | 返回boolean值,true表示添加成功                           |
| offer(value, timeout, TimeUnit unit) | 超时退出。若队列满了，会阻塞一段时间，超过时间后就会退出 |
| put(value)                           | 队列满了会阻塞                                           |

put()

```java
public void put(E e) throws InterruptedException {
    //判断是否为null
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        //count为队列中的元素个数，如果队列中的元素等于数组长度，则添加到notFull条件队列中进行阻塞等待,直到notFull.signal()唤醒
        while (count == items.length)
            notFull.await();
        //添加元素并唤醒notEmpty条件队列中的阻塞线程
        enqueue(e);
    } finally {
        lock.unlock();
    }
}
```

enqueue(E x)

```java
private void enqueue(E x) {
    // assert lock.getHoldCount() == 1;
    // assert items[putIndex] == null;
    final Object[] items = this.items;
    items[putIndex] = x;
    if (++putIndex == items.length)
        putIndex = 0;
    count++;
    notEmpty.signal();
}
```

##### 获取方法

| 方法                           |                    |
| ------------------------------ | ------------------ |
| remove()                       | 如果为空，抛出异常 |
| poll()                         | 如果为空，返回null |
| take()                         | 为空阻塞           |
| queue.poll(4,TimeUnit.SECONDS) | 超时等待           |

take()

```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        while (count == 0)
            //队列为空等待
            notEmpty.await();
        return dequeue();
    } finally {
        lock.unlock();
    }
}
```

```java
private E dequeue() {
    // assert lock.getHoldCount() == 1;
    // assert items[takeIndex] != null;
    final Object[] items = this.items;
    @SuppressWarnings("unchecked")
    E x = (E) items[takeIndex];
    items[takeIndex] = null;
    if (++takeIndex == items.length)
        takeIndex = 0;
    count--;
    if (itrs != null)
        itrs.elementDequeued();
    //唤醒notFull中条件队列阻塞的线程
    notFull.signal();
    return x;
}
```