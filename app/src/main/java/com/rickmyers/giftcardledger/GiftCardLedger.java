package com.rickmyers.giftcardledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rickmyers.giftcardledger.database.GiftCardCursorWrapper;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;
import com.rickmyers.giftcardledger.database.GiftCardDBHelper;

/**
 * A gift card ledger. This singleton holds the current state of the ledger and provides persistence.
 *
 * @author Rick Myers
 */
public class GiftCardLedger {

    // Only one GiftCardLedger may exist at any given time!
    private static GiftCardLedger sGiftCardLedger;

    private Context mContext;
    private SQLiteDatabase mDatabase;


    /**
     * Returns a new {@link GiftCardLedger} object if one doesn't exist. Otherwise, returns the
     * existing GiftCardLedger.
     *
     * @param context the Application's context
     * @return the new/existing {@link GiftCardLedger}
     */
    public static GiftCardLedger get(Context context) {
        if (sGiftCardLedger == null) {
            sGiftCardLedger = new GiftCardLedger(context);
        }
        return sGiftCardLedger;
    }

    /**
     * Private class constructor
     *
     * @param context Context of the hosting activity
     */
    private GiftCardLedger(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new GiftCardDBHelper(mContext).getWritableDatabase();
    }

    /**
     * Adds a card to the database.
     *
     * @param card the {@link GiftCard} to be added
     */
    public void addCard(GiftCard card) {
        ContentValues values = getContentValues(card);
        mDatabase.insert(GiftCardTable.NAME, null, values);
    }

    /**
     * Returns the current list of {@link GiftCard}.
     *
     * @return {@link List<GiftCard>}
     */
    public List<GiftCard> getGiftCardList() {
        // create a new list
        List<GiftCard> cards = new ArrayList<>();
        // query database for all rows
        GiftCardCursorWrapper cursor = queryGiftCards(null, null);

        // try to move through rows and add cards to list
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cards.add(cursor.getGiftCard());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return cards;
    }

    /**
     * Query database for a {@link GiftCard} entry
     *
     * @param id the id to be queried
     * @return the {@link GiftCard} if found, otherwise null
     */
    public GiftCard getGiftCard(UUID id) {
        // query database for a gift card that matches the given UUID
        GiftCardCursorWrapper cursor = queryGiftCards(
                GiftCardTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        // return the gift card if found, otherwise null
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getGiftCard();
        } finally {
            cursor.close();
        }
    }

    /**
     * Removes a card from the database.
     *
     * @param id the UUID of the card to be removed.
     */
    public void removeGiftCard(UUID id) {
        mDatabase.delete(GiftCardTable.NAME, GiftCardTable.Cols.UUID + " = ?", new String[]{id.toString()});
    }

    /**
     * Update a {@link GiftCard} in the database.
     *
     * @param card the {@link GiftCard} to be updated.
     */
    public void updateGiftCard(GiftCard card) {
        String uuidString = card.getId().toString();
        ContentValues values = getContentValues(card);

        mDatabase.update(GiftCardTable.NAME, values,
                GiftCardTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * Returns a {@link ContentValues} object that can be used to the column of any {@link GiftCard}.
     *
     * @param card the {@link GiftCard} to be converted to a ContentValue object
     * @return a {@link ContentValues} object that can be used with the database.
     */
    private static ContentValues getContentValues(GiftCard card) {
        ContentValues values = new ContentValues();
        values.put(GiftCardTable.Cols.UUID, card.getId().toString());
        values.put(GiftCardTable.Cols.NAME, card.getName());
        values.put(GiftCardTable.Cols.BALANCE, card.getBalance().toString());
        values.put(GiftCardTable.Cols.START_DATE, card.getStartDate().toString());

        return values;
    }

    /**
     * Returns a {@link GiftCardCursorWrapper} that can be used to easily parse data returned from the database.
     *
     * @param whereClause the sqlite WHERE clause
     * @param whereArgs   the sqlite WHERE arguments
     * @return a {@link GiftCardCursorWrapper}
     */
    private GiftCardCursorWrapper queryGiftCards(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                GiftCardTable.NAME,
                null, //Select * columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new GiftCardCursorWrapper(cursor);
    }
}