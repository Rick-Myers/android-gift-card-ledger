package com.rickmyers.giftcardledger;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GiftCardLedger {
    private static GiftCardLedger sGiftCardLedger;
    private List<GiftCard> mGiftCardList;

    public static GiftCardLedger get(Context context) {
        if (sGiftCardLedger == null) {
            sGiftCardLedger = new GiftCardLedger(context);
        }
        return sGiftCardLedger;
    }

    private GiftCardLedger(Context context) {
        mGiftCardList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            GiftCard tempCard = new GiftCard();
            tempCard.setName("Card Test #" + i);
            tempCard.setBalance(10.00f);
            mGiftCardList.add(tempCard);
        }
    }

    public List<GiftCard> getGiftCardList() {
        return mGiftCardList;
    }

    public GiftCard getGiftCard(UUID id) {
        for (GiftCard card : mGiftCardList) {
            if (card.getId().equals(id)) {
                return card;
            }
        }
        return null;
    }
}