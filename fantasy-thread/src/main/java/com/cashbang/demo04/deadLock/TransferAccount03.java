package com.cashbang.demo04.deadLock;

/**
 * 破坏循环等待条件
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class TransferAccount03 implements Runnable {

    /**
     * 转出账户
     */
    private Account fromAccont;

    /**
     * 转入账户
     */
    private Account toAccount;

    private int amount;


    public TransferAccount03(Account fromAccont, Account toAccount, int amount) {
        this.fromAccont = fromAccont;
        this.toAccount = toAccount;
        this.amount = amount;
    }

    @Override
    public void run() {
        Account left = fromAccont;
        Account right = fromAccont;
        if(fromAccont.hashCode() > toAccount.hashCode()){
            left = toAccount;
            right=fromAccont;
        }
        while (true){
            synchronized (left){
                synchronized (right){
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
        Thread a = new Thread(new TransferAccount03(fromAccount,toAccount,10));
        Thread b = new Thread(new TransferAccount03(toAccount,fromAccount,30));
        a.start();
        b.start();

    }

}
