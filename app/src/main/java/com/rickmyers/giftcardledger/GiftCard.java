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
    private BigDecimal mBalance;
    private List<List<String>> mHistory;
    private String mHistoryTableName;
    private String mLastTransaction;

    //testing
    private int mListPosition;

    /**
     * Class constructor
    public GiftCard(String name, BigDecimal startBalance) {
        this(name,
                startBalance,
                UUID.randomUUID(),
                "History_" + new Date().getTime());
    }*/


    //testing
    /**
     * Class constructor
     */
    public GiftCard(String name, BigDecimal startBalance, int listPosition) {
        this(name,
                startBalance,
                UUID.randomUUID(),
                "History_" + new Date().getTime(),
                listPosition);
    }


    /**
     * Class constructor specifying an existing Gift card.
     *
     * @param name the name of the card
     * @param balance the balance on the card.
     * @param id unique UUID used for identifying cards
     * @param historyTableName name of the card's transaction history table
     */
    public GiftCard(String name, BigDecimal balance, UUID id, String historyTableName, int listPosition) {
        mId = id;
        mName = name;
        mBalance = balance;
        mLastTransaction = "+" + getFormattedBalance(balance);
        mHistoryTableName = historyTableName;

        //testing
        mListPosition = listPosition;
    }

    public int getListPosition(){
        return mListPosition;
    }

    public void setListPosition(int position){
        mListPosition = position;
    }


    /**
     * Returns date formatted.
     *
     * @return Date formatted to be Month/Day/Year ex. Jan 1, 2019
     */
    public static String dateFormatter() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
    }

    /**
     * Transaction history getter.
     *
     * @return transaction history list
     */
    public List<List<String>> getHistory() {
        return mHistory;
    }

    /**
     * Transaction history setter.
     *
     * @param history list of lists that contain transaction history
     */
    public void setHistory(List<List<String>> history) {
        mHistory = history;
    }

    /**
     * Appends a new history string to the current list of transaction history.
     *
     * @param newHistory latest list of strings of transaction history
     */
    public void appendHistory(List<String> newHistory){
        mHistory.add(newHistory);
    }

    /**
     * Decreases current balance.
     *
     * @param value {@link BigDecimal} to be subtracted from current balance.
     * @return current balance if chaining is needed.
     */
    public BigDecimal subtractFromBalance(BigDecimal value){
        mBalance = mBalance.subtract(value);
        //Gift card balances can't be negative, how would that work?
        if (mBalance.compareTo(new BigDecimal(0)) < 0)
        {
            mBalance = new BigDecimal(0);
        }
        mLastTransaction = "-" + getFormattedBalance(value);
        createHistoryTransaction(mLastTransaction);
        return mBalance;
    }

    /**
     * Increases current balance.
     *
     * @param value {@link BigDecimal} to be added to current balance.
     * @return current balance if chaining is needed.
     */
    public BigDecimal addToBalance(BigDecimal value){
        mBalance = mBalance.add(value);
        mLastTransaction = "+" + getFormattedBalance(value);
        createHistoryTransaction(mLastTransaction);
        return mBalance;
    }

    /**
     * Creates a list of string that contains the data needed to represent a transaction
     * when the card's balance is increased or decreased.
     */
    private void createHistoryTransaction(String transac) {
        List<String> newHistory = new ArrayList<>();
        newHistory.add(dateFormatter());
        newHistory.add(transac);
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
     * Returns the {@link BigDecimal} representation of the gift card's <i>current</i> balance.
     *
     * @return the current balance of the gift card.
     */
    public BigDecimal getBalance() {
        return mBalance;
    }

    public String getLastTransaction(){
        return mLastTransaction;
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
     * Returns the name of the card'd history table.
     *
     * @return the name of the card's history table.
     */
    public String getHistoryTableName() {
        return mHistoryTableName;
    }

}
