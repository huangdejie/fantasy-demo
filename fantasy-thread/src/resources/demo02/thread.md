#### 问题

```java
private static int count = 0;

public static void incr(){
    try {
        Thread.sleep(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    count++;
}

public static void main( String[] args ) throws InterruptedException {
    for (int i = 0; i < 1000; i++) {
        new Thread(()->{
            App.incr();
        }).start();
    }
    Thread.sleep(3000);
    System.out.println("运行结果:"+count);
}

```

结果为<=count的随机数

原因：可见性、原子性



#### count++操作的指令

```java
12: getstatic     #5                  // Field count:I
15: iconst_1
16: iadd
17: putstatic     #5                  // Field count:I

```

其操作不是原子性的



#### 锁的存储(对象头)

对象头一共占128位，其中mark word占64位，klass pointer占64位

![img](https://upload-images.jianshu.io/upload_images/5679451-5ac17f8990160b1e.png?imageMogr2/auto-orient/strip|imageView2/2/w/960/format/webp)

Mark Word的具体信息（markOop.hpp文件中）

```c++
//  32 bits:
//  --------
//           hash:25 ------------>| age:4    biased_lock:1 lock:2 (normal object) 普通对象
//           JavaThread*:23 epoch:2 age:4    biased_lock:1 lock:2 (biased object)偏向锁对象
//           size:32 ------------------------------------------>| (CMS free block)
//           PromotedObject*:29 ---------->| promo_bits:3 ----->| (CMS promoted object)
//
//  64 bits:
//  --------
//  unused:25 hash:31 -->| unused:1   age:4    biased_lock:1 lock:2 (normal object)普通对象
//  JavaThread*:54 epoch:2 unused:1   age:4    biased_lock:1 lock:2 (biased object)偏向锁
//  PromotedObject*:61 --------------------->| promo_bits:3 ----->| (CMS promoted object)
//  size:64 ----------------------------------------------------->| (CMS free block)

注意：在偏向锁对象头部信息中是没有地儿存hash值的，一旦计算hash值则偏向锁将升级为轻量级锁

```

附加知识：

​			大端存储：数据的高字节保存在内存的低地址中，低字节保存在内存的高地址中。这样的存储模式有点					类似于将数据当作字符串顺序处理：地址由小向大增加，而数据从高位往地位放

​			小端存储：数据的高字节保存在内存的高地址中，而数据的低字节保存在内存的地址中。这种存储模式将					地址的高低和数据的位权有效地结合起来，高地址部分权值高，低地址部分权值低

##### 实例：

```java
public class SynchronizedDemo {
    public static void main(String[] args) {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        synchronized (synchronizedDemo){
          System.out.println("locking");				                                  			System.out.println(ClassLayout.parseInstance(synchronizedDemo).toPrintable());
        }
    }
}
```

运行后的类布局为：96位->压缩以后的

```java
com.cashbang.demo02.SynchronizedDemo object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           48 f8 47 02 (01001000 11111000 01000111 00000010) (38271048)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           05 c0 00 f8 (00000101 11000000 00000000 11111000) (-134168571)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
```

上述采用大端存储。则实际头部为

​	00000000 00000000 00000000 00000000 00000010 01000111 11111000 01001000

末位为00即轻量级锁



###### 偏向锁：默认关闭

在大多数情况下，锁不仅仅不存在多线程的竞争，而且总是由同一个线程多次获得。在这个背景下就设计了偏向锁。偏向锁，顾名思义，就是锁偏向于某个线程

当一个线程访问加了同步锁的代码块时，会在对象头中存储当前线程ID，后续这个线程进入和退出这段同步代码块时，不需要再次枷锁和释放锁。而是直接比较对象头里面是否存储了指向当前线程的偏向锁。如果相等表示偏向锁是偏向于当前线程的，无需再尝试获取锁了，引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径。(偏向锁的目的是消除数据在无竞争情况下的同步原语，进一步提高程序的运行性能)

加入jvm参数-XX:+UseBiasedLocking -XX:BiasedLockingStartupDelay=0再次执行以上代码

则运行后的类布局为

```java
com.cashbang.demo02.SynchronizedDemo object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           05 28 37 03 (00000101 00101000 00110111 00000011) (53946373)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           05 c0 00 f8 (00000101 11000000 00000000 11111000) (-134168571)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
```

头部信息为00000000 00000000 00000000 00000000 00000011 00110111 00101000 00000101

末位为101即1为偏向锁标识，01代表偏向锁



偏向锁的释放：

​		偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动去释放偏向锁。		偏向锁的撤销，需要等待全局安全点(在这个时间点上没有字节码正在执行)，它会首先暂停拥有偏向锁的线		程，判断锁对象是否处于被锁定状态，撤销偏向锁后恢复到未锁定(01)或轻量级锁(00)的状态。



###### 轻量级锁：

如果偏向锁关闭或者当前偏向锁已经被其他线程获取，那么这个时候如果有线程去抢占同步锁时，锁会升级到轻量级锁。

```java
public static void main(String[] args) {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        new Thread(()->{
            synchronized (synchronizedDemo){
                System.out.println("t1---locking");
                System.out.println(ClassLayout.parseInstance(synchronizedDemo).toPrintable());
                System.out.println("***************************");

            }
        },"t1").start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            synchronized (synchronizedDemo){
                System.out.println("t2---locking");
                System.out.println(ClassLayout.parseInstance(synchronizedDemo).toPrintable());
                System.out.println("***************************");

            }
        },"t2").start();
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```



```java
t1---locking
com.cashbang.demo02.SynchronizedDemo object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           f0 f1 51 20 (11110000 11110001 01010001 00100000) (542241264)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           05 c0 00 f8 (00000101 11000000 00000000 11111000) (-134168571)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
Space losses: 0 bytes internal + 4 bytes external = 4 bytes total

***************************
t2---locking
com.cashbang.demo02.SynchronizedDemo object internals:
 OFFSET  SIZE   TYPE DESCRIPTION                               VALUE
      0     4        (object header)                           20 ee 51 20 (00100000 11101110 01010001 00100000) (542240288)
      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4        (object header)                           05 c0 00 f8 (00000101 11000000 00000000 11111000) (-134168571)
     12     4        (loss due to the next object alignment)
Instance size: 16 bytes
```

末位都是000为轻量级锁

轻量级加锁过程：原博客地址(https://www.jianshu.com/p/31b6a0b1b84b)

1、在进入同步块时，如果同步对象锁状态为无锁状态(锁标志位为01状态，是否偏向锁为0)，虚拟机首先将在当前线程的栈帧中建立一个名为锁记录(Lock Record)的空间，用于存储锁对象目前的Mark Word的拷贝(官方称为Displaced Mark Word)。此时线程堆栈与对象头的状态如图

![img](https://upload-images.jianshu.io/upload_images/5679451-803afd4d7b40fbf5.png?imageMogr2/auto-orient/strip|imageView2/2/w/1000/format/webp)

2、拷贝对象头中的Mark Word复制到锁记录

3、拷贝成功后，虚拟机将使用CAS操作尝试将对象的Mark Word更新为指向Lock Record的指针，并将Lock Record里的owner指针指向object mark word。如果更新成功，则执行步骤4，否则执行5

4、如果更新动作成功了，那么这个线程就拥有了该对象的锁，并且对象Mark Word的锁标识位置为00，即表示此对象处于轻量级锁定状态，这时候线程堆栈与对象头的状态如图所示。

![img](https://upload-images.jianshu.io/upload_images/5679451-581fde9b5615c25c.png?imageMogr2/auto-orient/strip|imageView2/2/w/1000/format/webp)

5、如果这个更新操作失败了，虚拟机首先会检查对象的Mark Word是否执行当前线程的栈帧，如果是就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行。否则说明多个线程竞争锁，轻量级锁就要膨胀为重量级锁，锁标识的状态变为10，Mark Word中存储的就是指向重量级锁的指针，后面等待锁的线程也要进入阻塞状态。而当前线程便尝试使用自选来获取锁，自选就是为了不让线程阻塞，而采用循环去获取锁的过程。



轻量级锁解锁：

​	轻量级解锁时，会使用CAS操作来讲Displaced Mark Word替换位对象头，如果成功，则表示没有竞争发生。如果失败，表示当前锁存在竞争，锁就会膨胀成重量级锁。

###### 重量级锁：

​	多个线程竞争同一个锁的时候，虚拟机会阻塞加锁失败的线程，并且在目标被释放的时候，唤醒这些线程

​	java线程的阻塞以及唤醒，都是依靠操作系统来完成的：os pthread_mutex_lock()

​	升级为重量级锁时，锁标志的状态值为10，此时Mark Word中存储的是指向重量级锁的指针，此时等待锁的线程都会进入阻塞状态

```java
每一个java对象都会与一个监视器monitor关联，我们可以将它理解成一把锁，当一个线程想要执行一段被synchronzied修饰的同步方法或者代码块时，该线程得先获取到synchronized修饰的对象对应的monitor。
monitorenter表示去获得一个对象监视器。monitorexit表示释放monitor监视器的所有权，使得其他被阻塞的线程可以尝试去获取这个监视器
monitor依赖操作系统的MutexLock(互斥锁)来实现的，线程被阻塞后便进入内核(linux)调度状态，这个会导致系统在用户态与内核态之间来回切换，严重影响锁的性能。
任意线程对Object（Object由synchronized保护）的访问，首先得获得Object的监视器。如果获取失败，线程进入同步队列，线程状态变为BLOCKED。当访问Object的前驱(获得了锁的线程)释放了锁，则该释放操作唤醒阻塞在同步队列中的线程，使其重新尝试对监视器的获取
```



​	