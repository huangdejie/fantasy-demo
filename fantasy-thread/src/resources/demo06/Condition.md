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

