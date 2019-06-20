package com.rickmyers.giftcardledger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rickmyers.giftcardledger.GiftCard;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A wrapper class for {@link CursorWrapper}.
 *
 * @author Rick Myers
 */
public class GiftCardCursorWrapper extends CursorWrapper {

    /**
     * Class constructor.
     *
     * @param cursor a reference to the Cursor object that is currently open on the database.
     */
    public GiftCardCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * Queries the database and returns a {@link GiftCard}.
     *
     * @return a new {@link GiftCard}
     */
    public GiftCard getGiftCard() {
        // grab card data from database
        String uuidString = getString(getColumnIndex(GiftCardTable.Cols.UUID));
        String name = getString(getColumnIndex(GiftCardTable.Cols.NAME));
        String balance = getString(getColumnIndex(GiftCardTable.Cols.BALANCE));
        String historyTable = getString(getColumnIndex(GiftCardTable.Cols.HISTORY_TABLENAME));

        // create a card with data
        GiftCard card = new GiftCard(name, new BigDecimal(balance), UUID.fromString(uuidString), historyTable);

        return card;
    }

    /**
     * Queries the database and returns a the transaction history for a specific {@link GiftCard}.
     *
     * @return a list of transaction history
     */
    public List<String> getHistory() {
        // grab transaction history from database
        String date = getString(getColumnIndex(GiftCardDbSchema.HistoryTable.Cols.DATE));
        String transaction = getString(getColumnIndex(GiftCardDbSchema.HistoryTable.Cols.TRANSAC));
        String balance = getString(getColumnIndex(GiftCardDbSchema.HistoryTable.Cols.BALANCE));

        // create a list with history
        List<String> history = new ArrayList<>();
        history.add(date);
        history.add(transaction);
        history.add(balance);

        // return transaction history list
        return history;
    }
}
