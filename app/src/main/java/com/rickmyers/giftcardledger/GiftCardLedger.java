package com.rickmyers.giftcardledger;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GiftCardLedger {
    private static GiftCardLedger sGiftCardLedger;
    private List<GiftCard> mGiftCardList;
    private Map<UUID, GiftCard> mGiftCardHashMap;

    public static GiftCardLedger get(Context context) {
        if (sGiftCardLedger == null) {
            sGiftCardLedger = new GiftCardLedger(context);
        }
        return sGiftCardLedger;
    }

    private GiftCardLedger(Context context) {
        mGiftCardHashMap = new LinkedHashMap<>();
        /*for (int i = 0; i < 10; i++){
            GiftCard tempCard = new GiftCard();
            tempCard.setName("Card Test #" + i);
            tempCard.setBalance(new BigDecimal(25.00));
            mGiftCardHashMap.put(tempCard.getId(), tempCard);
        }*/
    }

    public void addCard(GiftCard card){
        mGiftCardHashMap.put(card.getId(), card);
    }

    public List<GiftCard> getGiftCardList() {
        return new ArrayList<>(mGiftCardHashMap.values());
    }

    public GiftCard getGiftCard(UUID id) {
        return mGiftCardHashMap.get(id);
    }

    public GiftCard removeGiftCard(UUID id){
        GiftCard test = mGiftCardHashMap.remove(id);

        return test;
    }
}