package com.rickmyers.giftcardledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.rickmyers.giftcardledger.database.GiftCardCursorWrapper;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.HistoryTable;
import com.rickmyers.giftcardledger.database.GiftCardDBHelper;
import com.rickmyers.giftcardledger.database.HistoryCursorWrapper;
import com.rickmyers.giftcardledger.database.HistoryDBHelper;

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

    private String mdate;


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
        // Create gift card content values and insert new card into DB
        ContentValues values = getContentValues(card);
        mDatabase.insert(GiftCardTable.NAME, null, values);

        // Create gift card history table and content values and insert values into history database
        createHistoryTable(card);
        ContentValues history_values = getHistoryContentValues(card);
        mDatabase.insert(card.getHistoryTableName(), null, history_values);
    }

    private void createHistoryTable(GiftCard card) {
        String query = "create table " + card.getHistoryTableName() + "(" +
                " _id integer primary key autoincrement, " +
                HistoryTable.Cols.DATE + ", " +
                HistoryTable.Cols.BALANCE +
                ")";
        mDatabase.execSQL(query);
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
                GiftCard temp = cursor.getGiftCard();
                try {
                    List<List<String>> tempHistory = getHistoryList(temp);
                    temp.setHistory(tempHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cards.add(temp);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        /*for (GiftCard x: cards){
            try {
                List<List<String>> history = getHistoryList(x);
                x.setHistory(history);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }*/

        return cards;
    }


    public List<List<String>> getHistoryList(GiftCard card) {
        // create a new list
        List<List<String>> history = new ArrayList<>();
        // query database for all rows
        GiftCardCursorWrapper cursor = queryHistory(null, null, card);

        // try to move through rows and add history to list
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                history.add(cursor.getHistory());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        // returns all transactions made on gift card
        return history;
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
            //todo testing
            GiftCard card = cursor.getGiftCard();
            List<List<String>> cardHistory = getHistoryList(card);
            card.setHistory(cardHistory);
            return card;
            //return cursor.getGiftCard();
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
        GiftCard card = getGiftCard(id);

        String query = "DROP TABLE IF EXISTS " + card.getHistoryTableName();
        mDatabase.execSQL(query);

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

        String tableName = card.getHistoryTableName();
        ContentValues historyValues = getHistoryContentValues(card);
        mDatabase.insert(tableName, null, historyValues);
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
        values.put(GiftCardTable.Cols.HISTORY_TABLENAME, card.getHistoryTableName());

        return values;
    }

    private static ContentValues getHistoryContentValues(GiftCard card) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.DATE, GiftCard.dateFormatter());
        values.put(HistoryTable.Cols.BALANCE, card.getBalance().toString());

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

    private GiftCardCursorWrapper queryHistory(String whereClause, String[] whereArgs, GiftCard card) {
        Cursor cursor = mDatabase.query(
                card.getHistoryTableName(),
                null, //Select * columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new GiftCardCursorWrapper(cursor);
    }

/*    *//**
     * Returns the gift card's photo
     *
     * @param card the gift card
     * @return the gift card's photo
     *//*
    public File getPhotoFile(GiftCard card){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, card.getPhotoFilename());
    }*/
}