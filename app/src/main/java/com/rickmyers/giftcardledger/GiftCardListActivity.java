package com.rickmyers.giftcardledger;

import android.support.v4.app.Fragment;

public class GiftCardListActivity extends SingleFragAndFAB {

    @Override
    protected Fragment createFragment() {
        return new GiftCardListFragment();
    }
}
