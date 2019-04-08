package com.rickmyers.giftcardledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rickmyers.giftcardledger.database.GiftCardCursorWrapper;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;
import com.rickmyers.giftcardledger.database.GiftCardDBHelper;

public class GiftCardLedger {
    private static GiftCardLedger sGiftCardLedger;
    private List<GiftCard> mGiftCardList;
    private Map<UUID, GiftCard> mGiftCardHashMap;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static GiftCardLedger get(Context context) {
        if (sGiftCardLedger == null) {
            sGiftCardLedger = new GiftCardLedger(context);
        }
        return sGiftCardLedger;
    }

    private GiftCardLedger(Context context) {
        //mGiftCardHashMap = new LinkedHashMap<>();
        mContext = context.getApplicationContext();
        mDatabase = new GiftCardDBHelper(mContext).getWritableDatabase();
        /*for (int i = 0; i < 10; i++){
            GiftCard tempCard = new GiftCard();
            tempCard.setName("Card Test #" + i);
            tempCard.setBalance(new BigDecimal(25.00));
            mGiftCardHashMap.put(tempCard.getId(), tempCard);
        }*/
    }

    public void addCard(GiftCard card){
        //mGiftCardHashMap.put(card.getId(), card);

        //---
        ContentValues values = getContentValues(card);
        mDatabase.insert(GiftCardTable.NAME, null, values);
    }

    public List<GiftCard> getGiftCardList() {

        List<GiftCard> cards = new ArrayList<>();

        GiftCardCursorWrapper cursor = queryGiftCards(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                cards.add(cursor.getGiftCard());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return cards;

        //return new ArrayList<>(mGiftCardHashMap.values());
    }

    public GiftCard getGiftCard(UUID id) {
        GiftCardCursorWrapper cursor = queryGiftCards(
                GiftCardTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getGiftCard();
        } finally {
            cursor.close();
        }
    }

    public void removeGiftCard(UUID id){
        mDatabase.delete(GiftCardTable.NAME, GiftCardTable.Cols.UUID + " = ?", new String[] { id.toString() });
    }

    public void updateGiftCard(GiftCard card){
        String uuidString = card.getId().toString();
        ContentValues values = getContentValues(card);

        mDatabase.update(GiftCardTable.NAME, values,
                GiftCardTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(GiftCard card){
        ContentValues values = new ContentValues();
        values.put(GiftCardTable.Cols.UUID, card.getId().toString());
        values.put(GiftCardTable.Cols.NAME, card.getName());
        values.put(GiftCardTable.Cols.BALANCE, card.getBalance().toString());
        values.put(GiftCardTable.Cols.START_DATE, card.getStartDate().toString());

        return values;
    }

    private GiftCardCursorWrapper queryGiftCards(String whereClause, String[] whereArgs){
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