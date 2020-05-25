package com.cashbang.demo04.deadLock;

/**
 * @Author: huangdj
 * @Date: 2020/5/25
 */
public class Account {

    private int balance;
    private String accoutName;

    public Account(String accoutName,int balance) {
        this.balance = balance;
        this.accoutName = accoutName;
    }

    /**
     * 更新转出方的余额
     * @param amount
     */
    public void debit(int amount){
        this.balance -= amount;
    }

    /**
     * 更新转入方的余额
     * @param amount
     */
    public void credit(int amount){
        this.balance += amount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getAccoutName() {
        return accoutName;
    }

    public void setAccoutName(String accoutName) {
        this.accoutName = accoutName;
    }
}
