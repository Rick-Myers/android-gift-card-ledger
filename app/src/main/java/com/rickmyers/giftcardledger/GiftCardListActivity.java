package com.rickmyers.giftcardledger;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;

import java.util.UUID;

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
    private GiftCardLedger mGiftCardLedger;
    private int mLastSelected = -1;

    @Override
    protected Fragment createFragment() {
        return new GiftCardListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onGiftCardSelected(GiftCard card, int position) {
        Log.d(TAG, "onGiftCardSelected");
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = GiftCardPagerActivity.newIntent(this, card.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = GiftCardEditFragment.newInstance(card.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

        mLastSelected = position;
    }

    @Override
    public void onGiftCardDeleted() {
        if (findViewById(R.id.detail_fragment_container) != null){
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);
            if(fragment != null){
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        }

    }

    @Override
    public void onGiftCardUpdated(GiftCard card) {
        Log.d(TAG, "onGiftCardUpdated");
        GiftCardListFragment listFragment = (GiftCardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateList(mLastSelected);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onGiftCardAdd() {
        Log.d(TAG, "onGiftCardAdd");
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = new Intent(this, GiftCardAddActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
        } else {
            Fragment newDetail = new GiftCardAddFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    private void updateListUI() {
        Log.d(TAG, "updateListUI");
        GiftCardListFragment listFragment = (GiftCardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onGiftCardAdded(GiftCard card) {
        Log.d(TAG, "onGiftCardAdded");
        mGiftCardLedger = GiftCardLedger.get(this);
        GiftCardLedger.get(this).addCard(card);
        GiftCardListFragment listFragment = (GiftCardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        int position;
        if(mGiftCardLedger.getGiftCardList().isEmpty()){
            listFragment.addCard(0, card);
            position = 0;
        } else {
            listFragment.addCard(mGiftCardLedger.getGiftCardList().size() - 1, card);
            position = mGiftCardLedger.getGiftCardList().size() - 1;
        }
        onGiftCardSelected(card, position);
        updateListUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD){
            Log.d(TAG, "onActivityResult");
            mGiftCardLedger = GiftCardLedger.get(this);
            GiftCardListFragment listFragment = (GiftCardListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            UUID id = (UUID) data.getSerializableExtra(GiftCardAddFragment.EXTRA_ADD);
            GiftCard card = mGiftCardLedger.getGiftCard(id);
            if(mGiftCardLedger.getGiftCardList().isEmpty()){
                listFragment.addCard(0, card);
            } else {
                listFragment.addCard(mGiftCardLedger.getGiftCardList().size() - 1, card);
            }
        }
    }
}
