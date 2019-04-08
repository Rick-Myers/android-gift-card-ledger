package com.rickmyers.giftcardledger.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rickmyers.giftcardledger.GiftCard;
import com.rickmyers.giftcardledger.database.GiftCardDbSchema.GiftCardTable;

import java.math.BigDecimal;
import java.util.UUID;

public class GiftCardCursorWrapper extends CursorWrapper {

    public GiftCardCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public GiftCard getGiftCard() {
        String uuidString = getString(getColumnIndex(GiftCardTable.Cols.UUID));
        String name = getString(getColumnIndex(GiftCardTable.Cols.NAME));
        String balance = getString(getColumnIndex(GiftCardTable.Cols.BALANCE));

        GiftCard card = new GiftCard(UUID.fromString(uuidString));
        card.setName(name);
        card.setBalance(new BigDecimal(balance));

        return card;
    }
}
