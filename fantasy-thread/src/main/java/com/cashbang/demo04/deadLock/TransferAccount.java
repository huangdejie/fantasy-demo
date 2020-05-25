package com.cashbang.demo04.deadLock;

/**
 * 死锁现象
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class TransferAccount implements Runnable {

    /**
     * 转出账户
     */
    private Account fromAccont;

    /**
     * 转入账户
     */
    private Account toAccount;

    private int amount;


    public TransferAccount(Account fromAccont, Account toAccount, int amount) {
        this.fromAccont = fromAccont;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    @Override
    public void run() {
        while (true){
            synchronized (fromAccont){
                synchronized (toAccount){
                    if(fromAccont.getBalance() >= amount){
                        fromAccont.debit(amount);
                        toAccount.credit(amount);
                    }
                }
            }
            System.out.println(fromAccont.getAccoutName()+"->"+fromAccont.getBalance());
            System.out.println(fromAccont.getAccoutName()+"->"+fromAccont.getBalance());
        }
    }

    public static void main(String[] args) {
        Account fromAccount = new Account("老刘",100000);
        Account toAccount = new Account("老张",300000);
        Thread a = new Thread(new TransferAccount(fromAccount,toAccount,10));
        Thread b = new Thread(new TransferAccount(toAccount,fromAccount,30));
        a.start();
        b.start();

    }

}
