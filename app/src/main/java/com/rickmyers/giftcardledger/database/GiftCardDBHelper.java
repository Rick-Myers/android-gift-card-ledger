package com.rickmyers.giftcardledger.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;

/**
 * An extension of the {@link SQLiteOpenHelper} class. Used to create and upgrade the database.
 *
 * @author Rick Myers
 */
public class GiftCardDBHelper extends SQLiteOpenHelper {
    //private static final int VERSION = 1;

    public static final String DATABASE_NAME = "gift_card.db";

    /**
     * Class constructor
     *
     * @param context the Application's context
     */
    public GiftCardDBHelper(Context context) {
        super(context, DATABASE_NAME, null, GiftCardDbSchema.VERSION);
    }

    /**
     * Creates the database is it doesn't already exist.
     *
     * @param db the SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // todo define column data types
        db.execSQL("create table " + GiftCardTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                GiftCardTable.Cols.UUID + ", " +
                GiftCardTable.Cols.NAME + ", " +
                GiftCardTable.Cols.BALANCE + ", " +
                //GiftCardTable.Cols.HISTORY + ", " +
                GiftCardTable.Cols.HISTORY_TABLENAME + ", " +
                GiftCardTable.Cols.LIST_POSITION +
                ")"
        );
    }

    /**
     * Upgrades database if a new version exists.
     *
     * @param db         the SQLite database
     * @param oldVersion the old version number
     * @param newVersion the new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
