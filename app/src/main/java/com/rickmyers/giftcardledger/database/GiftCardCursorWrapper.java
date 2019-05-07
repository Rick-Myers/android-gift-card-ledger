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
        // grab data from database
        String uuidString = getString(getColumnIndex(GiftCardTable.Cols.UUID));
        String name = getString(getColumnIndex(GiftCardTable.Cols.NAME));
        String balance = getString(getColumnIndex(GiftCardTable.Cols.BALANCE));
        String historyTable = getString(getColumnIndex(GiftCardTable.Cols.HISTORY_TABLENAME));

        // create a card with data
        GiftCard card = new GiftCard(name, new BigDecimal(balance), UUID.fromString(uuidString), historyTable);
        //card.setName(name);
        //card.setBalance(new BigDecimal(balance));

        // return card
        return card;
    }

    /**
     * Queries the database and returns a the transaction history for a specific {@link GiftCard}.
     *
     * @return transaction history
     */
    public List<String> getHistory() {
        String date = getString(getColumnIndex(GiftCardDbSchema.HistoryTable.Cols.DATE));
        String balance = getString(getColumnIndex(GiftCardDbSchema.HistoryTable.Cols.BALANCE));

        List<String> temp = new ArrayList<>();
        temp.add(date);
        temp.add(balance);

        // return list
        return temp;
    }
}
