package com.cashbang.demo04.deadLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 破坏不可抢占条件
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class TransferAccount02 implements Runnable {

    /**
     * 转出账户
     */
    private Account fromAccont;

    /**
     * 转入账户
     */
    private Account toAccount;

    private int amount;

    private Lock fromLock = new ReentrantLock();
    private Lock toLock = new ReentrantLock();



    public TransferAccount02(Account fromAccont, Account toAccount, int amount) {
        this.fromAccont = fromAccont;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    @Override
    public void run() {
        while (true){
            if (fromLock.tryLock()){
                if (toLock.tryLock()){
                    if(fromAccont.getBalance() >= amount){
                        fromAccont.debit(amount);
                        toAccount.credit(amount);
                    }
                }
            }
//            fromLock.lock();
//            toLock.lock();
//                    if(fromAccont.getBalance() >= amount){
//                        fromAccont.debit(amount);
//                        toAccount.credit(amount);
//                    }
//            fromLock.unlock();
//            toLock.unlock();
            System.out.println(fromAccont.getAccoutName()+"->"+fromAccont.getBalance());
            System.out.println(fromAccont.getAccoutName()+"->"+fromAccont.getBalance());
        }
    }

    public static void main(String[] args) {
        Account fromAccount = new Account("老刘",100000);
        Account toAccount = new Account("老张",300000);
        Thread a = new Thread(new TransferAccount02(fromAccount,toAccount,10));
        Thread b = new Thread(new TransferAccount02(toAccount,fromAccount,30));
        a.start();
        b.start();

    }

}
