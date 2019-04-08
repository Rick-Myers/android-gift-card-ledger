package com.rickmyers.giftcardledger;

import android.database.Cursor;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class GiftCard {

    private UUID mId;
    private String mName;
    private List<String> history;
    private Date mStartDate;
    private int mNumber;
    private BigDecimal mBalance, mStartBalance;


    public GiftCard() {
        this(UUID.randomUUID());
    }

    public GiftCard(UUID id){
        mId = id;
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

    public BigDecimal getBalance() {
        return mBalance;
    }

    public void setBalance(BigDecimal balance) {
        mBalance = balance;
    }

    public BigDecimal getStartBalance() {
        return mStartBalance;
    }

    public void setStartBalance(BigDecimal startBalance) {
        mStartBalance = startBalance;
    }

    public static String getFormattedBalance(BigDecimal balance){
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(balance);
    }


}
