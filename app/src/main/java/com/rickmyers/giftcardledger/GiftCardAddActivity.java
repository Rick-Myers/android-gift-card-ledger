package com.rickmyers.giftcardledger;

import android.support.v4.app.Fragment;

/**
 * An Activity used to add {@link GiftCard}
 *
 * @author Rick Myers
 */
public class GiftCardAddActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GiftCardAddFragment();
    }
}
