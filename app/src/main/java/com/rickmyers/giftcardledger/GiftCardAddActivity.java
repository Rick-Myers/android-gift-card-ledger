package com.rickmyers.giftcardledger;

import android.support.v4.app.Fragment;


public class GiftCardAddActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GiftCardAddFragment();
    }
}
