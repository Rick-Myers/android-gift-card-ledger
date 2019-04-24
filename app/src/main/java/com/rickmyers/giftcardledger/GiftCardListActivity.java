package com.rickmyers.giftcardledger;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * The main view of the application. This is a list Activity that is responsible for creating the {@link GiftCardListFragment}.
 *
 * @author Rick Myers
 */
public class GiftCardListActivity extends SingleFragmentActivity implements GiftCardListFragment.Callbacks, GiftCardEditFragment.Callbacks, GiftCardAddFragment.Callbacks {

    // logging tag
    private static final String TAG = "GiftCardListActivity";

    // todo maybe make constants elsewhere if both fragment and activity use it.
    private static final int REQUEST_ADD = 1;

    @Override
    protected Fragment createFragment() {
        return new GiftCardListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onGiftCardSelected(GiftCard card) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = GiftCardPagerActivity.newIntent(this, card.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = GiftCardEditFragment.newInstance(card.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onGiftCardUpdated(GiftCard card) {
        updateListUI();
    }

    @Override
    public void onGiftCardAdd() {

        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = new Intent(this, GiftCardAddActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        } else {
            Fragment newDetail = new GiftCardAddFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

        updateListUI();
    }

    private void updateListUI() {
        GiftCardListFragment listFragment = (GiftCardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onGiftCardAdded(GiftCard card) {
        Log.d(TAG, "onGiftCardAdded");
        GiftCardLedger.get(this).addCard(card);
        onGiftCardSelected(card);
        updateListUI();
    }
}
