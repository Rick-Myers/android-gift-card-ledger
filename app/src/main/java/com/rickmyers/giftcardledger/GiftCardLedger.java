package com.rickmyers.giftcardledger;

import android.content.Context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GiftCardLedger {
    private static GiftCardLedger sGiftCardLedger;
    // todo Think about using LinkedHashMap
    private List<GiftCard> mGiftCardList;
    private Map<UUID, GiftCard> mGiftCardHashMap;

    public static GiftCardLedger get(Context context) {
        if (sGiftCardLedger == null) {
            sGiftCardLedger = new GiftCardLedger(context);
        }
        return sGiftCardLedger;
    }

    private GiftCardLedger(Context context) {
//        mGiftCardList = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            GiftCard tempCard = new GiftCard();
//            tempCard.setName("Card Test #" + i);
//            tempCard.setBalance(10.00f);
//            mGiftCardList.add(tempCard);
//        }

        mGiftCardHashMap = new LinkedHashMap<>();
        for (int i = 0; i < 100; i++){
            GiftCard tempCard = new GiftCard();
            tempCard.setName("Card Test #" + i);
            tempCard.setBalance(23.45f);
            mGiftCardHashMap.put(tempCard.getId(), tempCard);
        }
    }

    public List<GiftCard> getGiftCardList() {
        //return mGiftCardList;
        return new ArrayList<>(mGiftCardHashMap.values());
    }

    public GiftCard getGiftCard(UUID id) {
        /*for (GiftCard card : mGiftCardList) {
            if (card.getId().equals(id)) {
                return card;
            }
        }
        return null;*/

        return mGiftCardHashMap.get(id);
    }
}