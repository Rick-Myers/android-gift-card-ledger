package com.rickmyers.giftcardledger;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.UUID;

/**
 * The main view of the application. This is a list Activity that is responsible for creating the {@link GiftCardListFragment}.
 *
 * @author Rick Myers
 */
public class GiftCardListActivity extends SingleFragmentActivity implements GiftCardListFragment.Callbacks, GiftCardEditFragment.Callbacks, GiftCardAddFragment.Callbacks, ColorPickerDialogListener {

    // logging tag
    private static final String TAG = "GiftCardListActivity";

    // todo maybe make constants elsewhere if both fragment and activity use it.
    private static final int REQUEST_ADD = 1;
    private GiftCardLedger mGiftCardLedger;
    private int mLastSelected = -1;

    // Give your color picker dialog unique IDs if you have multiple dialogs.
    public static final int DIALOG_ID_BACKGROUND = 0;
    public static final int DIALOG_ID_SYMBOL = 1;
    public static final int DIALOG_ID_FONT = 2;

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

    @Override
    public void onColorSelected(int dialogId, int color) {
        Log.d(TAG, "onColorSelected() called with: dialogId = [" + dialogId + "], color = [" + color + "]");
        GiftCardEditFragment editFragment = (GiftCardEditFragment) getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);
        GiftCard card = editFragment.returnCurrentCard();

        switch (dialogId) {
            case DIALOG_ID_BACKGROUND:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(GiftCardListActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                editFragment.changeColors(color, DIALOG_ID_BACKGROUND);
                card.setBackgroundColor(color);
                GiftCardLedger.get(this).updateGiftCardValues(card);
                onGiftCardUpdated(card);

                break;
            case DIALOG_ID_SYMBOL:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(GiftCardListActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                editFragment.changeColors(color, DIALOG_ID_SYMBOL);
                card.setSymbolColor(color);
                GiftCardLedger.get(this).updateGiftCardValues(card);
                onGiftCardUpdated(card);

                break;
            case DIALOG_ID_FONT:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(GiftCardListActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                editFragment.changeColors(color, DIALOG_ID_FONT);
                card.setFontColor(color);
                GiftCardLedger.get(this).updateGiftCardValues(card);
                onGiftCardUpdated(card);

                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Log.d(TAG, "onDialogDismissed() called with: dialogId = [" + dialogId + "]");
    }

}
