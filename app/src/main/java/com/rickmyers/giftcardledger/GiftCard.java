package com.rickmyers.giftcardledger;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
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
    private Date mStartDate;
    private BigDecimal mBalance, mStartBalance;

    /**
     * Class constructor
     */
    public GiftCard() {
        this(UUID.randomUUID());
    }

    /**
     * Class constructor specifying an existing UUID.
     *
     * @param id the existing {@link UUID}
     */
    public GiftCard(UUID id) {
        mId = id;
        mStartDate = new Date();
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
     * @return      the creation date
     */
    public Date getStartDate() {
        return mStartDate;
    }

    /**
     * Returns the {@link BigDecimal} representation of the gift card's <i>current</i> balance.
     *
     * @return      the current balance of the gift card.
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
     * @return      the starting balance of the gift card.
     */
    public BigDecimal getStartBalance() {
        return mStartBalance;
    }

    /**
     * Sets the <i>starting</i> balance of the gift card.
     *
     * @param startBalance
     */
    public void setStartBalance(BigDecimal startBalance) {
        mStartBalance = startBalance;
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
     * Returns a unique file name to be used for saving gift card photos.
     *
     * @return a unique file name
     */
    public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }

}
