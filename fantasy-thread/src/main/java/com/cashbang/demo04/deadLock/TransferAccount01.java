package com.cashbang.demo04.deadLock;

/**
 * 破坏占有且等待条件
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class TransferAccount01 implements Runnable {

    /**
     * 转出账户
     */
    private Account fromAccont;

    /**
     * 转入账户
     */
    private Account toAccount;

    private int amount;

    private Allocator allocator;


    public TransferAccount01(Account fromAccont, Account toAccount, int amount,Allocator allocator) {
        this.fromAccont = fromAccont;
        this.toAccount = toAccount;
        this.amount = amount;
        this.allocator = allocator;
    }

    @Override
    public void run() {
        while (true){
            if(allocator.apply(fromAccont,toAccount)) {
                try {
                    synchronized (fromAccont) {
                        synchronized (toAccount) {
                            if (fromAccont.getBalance() >= amount) {
                                fromAccont.debit(amount);
                                toAccount.credit(amount);
                            }
                        }
                    }
                    System.out.println(fromAccont.getAccoutName() + "->" + fromAccont.getBalance());
                    System.out.println(fromAccont.getAccoutName() + "->" + fromAccont.getBalance());
                }finally {
                    allocator.free(fromAccont,toAccount);
                }
            }
        }
    }

    public static void main(String[] args) {
        Account fromAccount = new Account("老刘",100000);
        Account toAccount = new Account("老张",300000);
        Allocator allocator1 = new Allocator();
        Thread a = new Thread(new TransferAccount01(fromAccount,toAccount,10,allocator1));
        Thread b = new Thread(new TransferAccount01(toAccount,fromAccount,30,allocator1));
        a.start();
        b.start();

    }

}
