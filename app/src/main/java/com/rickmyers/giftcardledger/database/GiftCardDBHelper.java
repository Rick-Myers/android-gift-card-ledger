package com.rickmyers.giftcardledger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;

public class GiftCardDBHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "gift_card.db";

    public GiftCardDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // todo define column data types
        db.execSQL("create table " + GiftCardTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                GiftCardTable.Cols.UUID + ", " +
                GiftCardTable.Cols.NAME + ", " +
                GiftCardTable.Cols.BALANCE + ", " +
                GiftCardTable.Cols.START_DATE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
