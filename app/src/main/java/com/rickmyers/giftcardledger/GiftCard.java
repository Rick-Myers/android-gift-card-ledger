package com.rickmyers.giftcardledger;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class GiftCard {

    private UUID mId;
    private String mName;
    private List<String> history;
    private Date mStartDate;
    private int mNumber;
    private float mBalance, mStartBalance;


    public GiftCard() {
        this.mId = UUID.randomUUID();
        mStartDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public float getBalance() {
        return mBalance;
    }

    public void setBalance(float balance) {
        mBalance = balance;
    }

    public float getStartBalance() {
        return mStartBalance;
    }

    public void setStartBalance(float startBalance) {
        mStartBalance = startBalance;
    }
}
