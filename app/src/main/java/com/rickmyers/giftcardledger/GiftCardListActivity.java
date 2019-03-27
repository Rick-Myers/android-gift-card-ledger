package com.rickmyers.giftcardledger;

import android.support.v4.app.Fragment;

public class GiftCardListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GiftCardListFragment();
    }
}
