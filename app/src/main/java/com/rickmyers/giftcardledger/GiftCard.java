package com.rickmyers.giftcardledger;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * A gift card.
 *
 * @author Rick Myers
 */
public class GiftCard {

    private UUID mId;
    private String mName;
    //private Date mStartDate;
    private BigDecimal mBalance; //mStartBalance;
    private List<List<String>> mHistory;
    private String mHistoryTableName;

    /**
     * Class constructor
     */
    public GiftCard(String name, BigDecimal startBalance) {
        this(name,
                startBalance,
                UUID.randomUUID(),
                "History_" + new Date().getTime());
    }

    /**
     * Class constructor specifying an existing Gift card.
     *
     * @param name
     * @param balance
     * @param id
     * @param historyTableName
     */
    public GiftCard(String name, BigDecimal balance, UUID id, String historyTableName) {
        mId = id;
        mName = name;
        mBalance = balance;
        mHistoryTableName = historyTableName;
    }

    public static String dateFormatter() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
    }

    public List<List<String>> getHistory() {
        return mHistory;
    }

    public void setHistory(List<List<String>> history) {
        mHistory = history;
    }

    public void appendHistory(List<String> newHistory){
        mHistory.add(newHistory);
    }

    public BigDecimal subtractFromBalance(BigDecimal value){
        mBalance = mBalance.subtract(value);
        //Gift card balances can't be negative, how would that work?
        if (mBalance.compareTo(new BigDecimal(0)) < 0)
        {
            mBalance = new BigDecimal(0);
        }
        createHistoryTransaction();
        return mBalance;
    }

    public BigDecimal addToBalance(BigDecimal value){
        mBalance = mBalance.add(value);
        createHistoryTransaction();
        return mBalance;
    }

    private void createHistoryTransaction() {
        List<String> newHistory = new ArrayList<>();
        newHistory.add(dateFormatter());
        newHistory.add(mBalance.toString());
        appendHistory(newHistory);
    }


    /**
     * Returns the gift card's {@link UUID}
     *
     * @return the gift card's {@link UUID}
     */
    public UUID getId() {
        return mId;
    }

    /**
     * Returns the name fo the gift card.
     *
     * @return the name of the gift card.
     */
    public String getName() {
        return mName;
    }

    /**
     * Sets the name of the gift card. Currently, this is only used when the card is created.
     *
     * @param name the {@link String} identifier of the gift card.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Returns the {@link Date} that the gift card was created on.
     *
     * @return the creation date
     */
    /*public Date getStartDate() {
        return mStartDate;
    }*/

    /**
     * Returns the {@link BigDecimal} representation of the gift card's <i>current</i> balance.
     *
     * @return the current balance of the gift card.
     */
    public BigDecimal getBalance() {
        return mBalance;
    }

    /**
     * Sets the <i>current</i> balance of the gift card.
     *
     * @param balance the balance to be set.
     */
    public void setBalance(BigDecimal balance) {
        mBalance = balance;
    }

    /**
     * Returns the {@link BigDecimal} representation of the gift card's <i>starting</i> balance.
     *
     * @return the starting balance of the gift card.
     */
/*    public BigDecimal getStartBalance() {
        return mStartBalance;
    }*/

    /**
     * Sets the <i>starting</i> balance of the gift card.
     *
     * @param startBalance
     */
/*    public void setStartBalance(BigDecimal startBalance) {
        mStartBalance = startBalance;
    }*/

    /**
     * Converts a given {@link BigDecimal} into the correct currency per the device's
     * {@link Locale}.
     *
     * @param balance the {@link BigDecimal} representation of the balance to be formatted.
     * @return the balance converted into a {@link String}
     */
    public static String getFormattedBalance(BigDecimal balance) {
        Locale locale = new Locale("en", "US");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(balance);
    }

    /**
     * Returns a unique file name to be used for saving gift card photos.
     *
     * @return a unique file name
     */
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public String getHistoryTableName() {
        return mHistoryTableName;
    }

}
