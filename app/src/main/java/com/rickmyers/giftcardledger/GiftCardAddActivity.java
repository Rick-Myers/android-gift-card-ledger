package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * An Activity used to add {@link GiftCard}
 *
 * @author Rick Myers
 */
public class GiftCardAddActivity extends SingleFragmentActivity implements GiftCardAddFragment.Callbacks {
    // logging tag
    private static final String TAG = "GiftCardAddActivity";

    public static final String EXTRA_ADD = "com.rickmyers.giftcardledger.add";

    @Override
    protected Fragment createFragment() {
        return new GiftCardAddFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onGiftCardAdded(GiftCard card) {
        Log.d(TAG, "onGiftCardAdded");

        // add the card to the database
        GiftCardLedger.get(this).addCard(card);

        // return intent to the calling activity with the results of the card add
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_ADD, card.getId());
        this.setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }
}
