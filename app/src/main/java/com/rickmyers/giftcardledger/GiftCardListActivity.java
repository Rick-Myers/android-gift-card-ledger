package com.rickmyers.giftcardledger;

import android.support.v4.app.Fragment;

/**
 * The main view of the application. This is a list Activity that is responsible for creating the {@link GiftCardListFragment}.
 *
 * @author Rick Myers
 */
public class GiftCardListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new GiftCardListFragment();
    }
}
