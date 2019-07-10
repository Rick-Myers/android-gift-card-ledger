package com.rickmyers.giftcardledger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.rickmyers.giftcardledger.utilities.CustomFragmentStatePagerAdapter;

import java.util.List;
import java.util.UUID;

/**
 * A Pager Activity that is used to page through gift cards while editing.
 *
 * @author Rick Myers
 */
public class GiftCardPagerActivity extends AppCompatActivity implements GiftCardEditFragment.Callbacks, ColorPickerDialogListener {

    private static final String EXTRA_CARD_ID = "com.rickmyers.giftcardledger.card_id";

    // logging tag
    private static final String TAG = "GiftCardPagerActivity";


    // Give your color picker dialog unique IDs if you have multiple dialogs.
    public static final int DIALOG_ID_BACKGROUND = 0;
    public static final int DIALOG_ID_SYMBOL = 1;
    public static final int DIALOG_ID_FONT = 2;


    private ViewPager mViewPager;
    private List<GiftCard> mGiftCards;

    private CustomFragmentStatePagerAdapter mPagerAdapter;

    /**
     * Creates layout, pager, and fills pager with {@link GiftCardEditFragment}.
     *
     * @param savedInstanceState the Bundle used to host Fragment data.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pager);

        // Get gift card ID from Intent
        UUID cardID = (UUID) getIntent().getSerializableExtra(EXTRA_CARD_ID);

        mViewPager = findViewById(R.id.card_view_pagers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        mGiftCards = GiftCardLedger.get(this).getGiftCardList();
        FragmentManager fragmentManager = getSupportFragmentManager();

        mPagerAdapter = new CustomFragmentStatePagerAdapter(fragmentManager, mGiftCards);

        mViewPager.setAdapter(mPagerAdapter);



        /*mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                GiftCard card = mGiftCards.get(i);
                return GiftCardEditFragment.newInstance(card.getId());
            }

            @Override
            public int getCount() {
                return mGiftCards.size();
            }
        });*/

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        // set the current item to the given UUID.
        // todo Can this be done faster instead of possibly searching through the entire list?
        for (int i = 0; i < mGiftCards.size(); i++) {
            if (mGiftCards.get(i).getId().equals(cardID)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }




    }

    /**
     * Returns a new {@link Intent} with an extra that contains the gift card's {@link UUID}.
     *
     * @param packageContext the Application's context
     * @param cardID         the UUID of the gift card that is being displayed
     * @return the new Intent
     */
    public static Intent newIntent(Context packageContext, UUID cardID) {
        Intent intent = new Intent(packageContext, GiftCardPagerActivity.class);
        intent.putExtra(EXTRA_CARD_ID, cardID);
        return intent;
    }

    @Override
    public void onGiftCardUpdated(GiftCard card) {

    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        Log.d(TAG, "onColorSelected() called with: dialogId = [" + dialogId + "], color = [" + color + "]");
        GiftCardEditFragment editFragment = (GiftCardEditFragment) mPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
        GiftCard card = mGiftCards.get(mViewPager.getCurrentItem());

        switch (dialogId) {
            case DIALOG_ID_BACKGROUND:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(GiftCardPagerActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                editFragment.changeColors(color, DIALOG_ID_BACKGROUND);
                card.setBackgroundColor(color);
                GiftCardLedger.get(this).updateGiftCard(card);

                break;
            case DIALOG_ID_SYMBOL:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(GiftCardPagerActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                editFragment.changeColors(color, DIALOG_ID_SYMBOL);
                card.setSymbolColor(color);
                GiftCardLedger.get(this).updateGiftCard(card);

                break;
            case DIALOG_ID_FONT:
                // We got result from the dialog that is shown when clicking on the icon in the action bar.
                Toast.makeText(GiftCardPagerActivity.this, "Selected Color: #" + Integer.toHexString(color), Toast.LENGTH_SHORT).show();
                editFragment.changeColors(color, DIALOG_ID_FONT);
                card.setFontColor(color);
                GiftCardLedger.get(this).updateGiftCard(card);

                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        Log.d(TAG, "onDialogDismissed() called with: dialogId = [" + dialogId + "]");
    }
}
