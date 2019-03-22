package com.rickmyers.giftcardledger;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;


public class GiftCardEditActivity extends SingleFragmentActivity {

    private static final String EXTRA_CARD_ID = "com.rickmyers.giftcardledger.card_id";

    public static Intent newIntent(Context packageContext, UUID cardID){
        Intent intent = new Intent(packageContext, GiftCardEditActivity.class);
        intent.putExtra(EXTRA_CARD_ID, cardID);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        UUID cardId = (UUID) getIntent().getSerializableExtra(EXTRA_CARD_ID);
        return GiftCardEditFragment.newInstance(cardId);
    }
}
