package com.cashbang.demo06.blockqueue;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author: huangdj
 * @Date: 2020/6/5
 */
public class ArrayBlockQueueDemo {

    private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(5);


    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            String message = "消息"+i;
            Producer producer = new Producer(message);
            producer.setName("t"+i);
            producer.start();
        }
//        this.wait();
        for(int i=0;i<10;i++){
            Consumer consumer = new Consumer();
            consumer.setName("c"+i);
            consumer.start();
        }
        Thread.sleep(500000);
    }

    static class Producer extends Thread{

        private String msg;

        public Producer(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
//            queue.add(msg);//队列满了抛出异常

//            Object returnValue = queue.offer(msg);//返回boolean值,true表示添加成功
//            System.out.println(Thread.currentThread().getName()+":"+returnValue);

//            try {
//                queue.offer(msg, 5,TimeUnit.SECONDS);//超时退出，当队列满了，会阻塞一段时间，如果超过一定的时间，生产者线程就会退出
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            try {
                queue.put(msg);//阻塞
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("生产者:"+Thread.currentThread().getName()+"生产消息:"+msg);

        }
    }

    static class Consumer extends Thread{

        @Override
        public void run() {
            try {
//                queue.remove();//为空抛出异常
//                queue.poll();//为空，返回null
//                String msgValue = queue.poll(4,TimeUnit.SECONDS);
                String msgValue = queue.take();
                System.out.println("消费者:"+Thread.currentThread().getName()+"消费消息:"+msgValue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
