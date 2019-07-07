package com.rickmyers.giftcardledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rickmyers.giftcardledger.database.GiftCardCursorWrapper;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.HistoryTable;
import com.rickmyers.giftcardledger.database.GiftCardDBHelper;

/**
 * A gift card ledger. This singleton holds the current state of the ledger and provides persistence.
 *
 * @author Rick Myers
 */
public class GiftCardLedger {

    // logging tag
    private static final String TAG = "GiftCardLedger";

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
        // Create gift card content values and insert new card into DB
        ContentValues values = getContentValues(card);
        mDatabase.insert(GiftCardTable.NAME, null, values);

        // Create gift card history table and content values and insert values into history database
        createHistoryTable(card);
        ContentValues history_values = getHistoryContentValues(card);
        mDatabase.insert(card.getHistoryTableName(), null, history_values);
    }

    /**
     * Creates the history table that will host transaction history for the {@link GiftCard}
     *
     * @param card the {@link GiftCard} that the history table will be associated with
     */
    private void createHistoryTable(GiftCard card) {
        String query = "create table " + card.getHistoryTableName() + "(" +
                " _id integer primary key autoincrement, " +
                HistoryTable.Cols.DATE + ", " +
                HistoryTable.Cols.TRANSAC + ", " +
                HistoryTable.Cols.BALANCE +
                ")";
        mDatabase.execSQL(query);
    }

    public int countDbRows(){
        long rows = DatabaseUtils.queryNumEntries(mDatabase, GiftCardTable.NAME);

        return (int)rows;
    }


    private int getHistoryTableLastRowId(GiftCard card){
        int id;

        GiftCardCursorWrapper cursor = queryHistory(null, null, card);

        try {
            cursor.moveToLast();
            id = cursor.getInt(0);
         } finally {
            cursor.close();
        }

        return id;
    }

    public void deleteLatestHistoryEntry(GiftCard card){

        int id = getHistoryTableLastRowId(card);

        if(id > 0){
            mDatabase.delete(card.getHistoryTableName(), HistoryTable.Cols.ID + " = ?", new String[]{Integer.toString(id)});
        }

        revertToPreviousBalance(card);

    }

    private void revertToPreviousBalance(GiftCard card) {
        String lastBalance = getLastBalance(card);
        card.setBalance(new BigDecimal(lastBalance));
        updateGiftCardValues(card);
    }

    private String getLastBalance(GiftCard card) {

        int maxId = getHistoryTableLastRowId(card);

        String whereClause = HistoryTable.Cols.ID + " = ?";
        String[] whereArgs = new String[]{Integer.toString(maxId)};
        GiftCardCursorWrapper cursor = queryHistory(whereClause, whereArgs, card);

        // create a new list
        List<List<String>> history = new ArrayList<>();

        // try to move through rows and add history to list
        try {
            cursor.moveToLast();
            history.add(cursor.getHistory());
        } finally {
            cursor.close();
        }

        List<String> lastHistoryEntry = history.get(0);
        String balance = lastHistoryEntry.get(2);
        Log.d(TAG, balance);
        return balance;
    }

    public void swapCardListPositions(GiftCard dragged, GiftCard target){
        // get current card positions before swapping
        int draggedPos = dragged.getListPosition();
        int targetPos = target.getListPosition();

        // swap the cards' list positions
        dragged.setListPosition(targetPos);
        target.setListPosition(draggedPos);

        // update the positions in the database
        updateGiftCardValues(dragged);
        updateGiftCardValues(target);
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

        return cards;
    }

    /**
     * Returns the full list of transaction history for the {@link GiftCard}
     *
     * @param card the gift card that is being used to request transaction history
     * @return a list of lists containing transaction history
     */
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
            GiftCard card = cursor.getGiftCard();
            List<List<String>> cardHistory = getHistoryList(card);
            card.setHistory(cardHistory);
            return card;
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
        int removedListPosition = card.getListPosition();

        //Drop the history transaction table for the card.
        String query = "DROP TABLE IF EXISTS " + card.getHistoryTableName();
        mDatabase.execSQL(query);

        //Delete the row related to the card from the database.
        mDatabase.delete(GiftCardTable.NAME, GiftCardTable.Cols.UUID + " = ?", new String[]{id.toString()});

        //Update remaining cards' list positions
        updateCardsListPositions(removedListPosition);

    }

    private void updateCardsListPositions(int removedListPosition){

        List<GiftCard> cards = getGiftCardList();

        for(GiftCard card: cards){
            int currentPosition = card.getListPosition();

            if (currentPosition > removedListPosition){
                card.setListPosition(--currentPosition);
            }

            updateGiftCardValues(card);
        }
    }


    /**
     * Update a {@link GiftCard} in the database.
     *
     * @param card the {@link GiftCard} to be updated.
     */
    public void updateGiftCard(GiftCard card) {
        updateGiftCardValues(card);

        String tableName = card.getHistoryTableName();
        ContentValues historyValues = getHistoryContentValues(card);
        mDatabase.insert(tableName, null, historyValues);
    }

    private void updateGiftCardValues(GiftCard card) {
        String uuidString = card.getId().toString();
        ContentValues values = getContentValues(card);

        mDatabase.update(GiftCardTable.NAME, values,
                GiftCardTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    /**
     * Returns a {@link ContentValues} object that can be used to store data in the database.
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

        //testing
        values.put(GiftCardTable.Cols.LIST_POSITION, card.getListPosition());

        return values;
    }

    /**
     * Returns a {@link ContentValues} object that can be used to store data in the database.
     *
     * @param card the {@link GiftCard} to be converted to a ContentValue object
     * @return a {@link ContentValues} object that can be used with the database.
     */
    private static ContentValues getHistoryContentValues(GiftCard card) {
        ContentValues values = new ContentValues();
        values.put(HistoryTable.Cols.DATE, GiftCard.dateFormatter());
        values.put(HistoryTable.Cols.TRANSAC, card.getLastTransaction());
        values.put(HistoryTable.Cols.BALANCE, card.getBalance().toString());

        return values;
    }


    /**
     * Returns a {@link GiftCardCursorWrapper} that can be used to parse data returned from the database.
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
                GiftCardTable.Cols.LIST_POSITION
        );
        return new GiftCardCursorWrapper(cursor);
    }

    /**
     * Returns a {@link GiftCardCursorWrapper} that can be used to parse data returned from the database.
     *
     * @param whereClause the sqlite WHERE clause
     * @param whereArgs   the sqlite WHERE arguments
     * @return a {@link GiftCardCursorWrapper}
     */
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
}