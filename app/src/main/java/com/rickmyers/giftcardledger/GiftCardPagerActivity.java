package com.rickmyers.giftcardledger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class GiftCardPagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.rickmyers.giftcardledger.card_id";

    private ViewPager mViewPager;
    private List<GiftCard> mGiftCards;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pager);

        UUID cardID = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = findViewById(R.id.card_view_pager);

        mGiftCards = GiftCardLedger.get(this).getGiftCardList();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                GiftCard card = mGiftCards.get(i);
                return GiftCardEditFragment.newInstance(card.getId());
            }

            @Override
            public int getCount() {
                return mGiftCards.size();
            }
        });

        for (int i = 0; i < mGiftCards.size(); i++) {
            if (mGiftCards.get(i).getId().equals(cardID)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
    public static Intent newIntent(Context packageContext, UUID cardID){
        Intent intent = new Intent(packageContext, GiftCardPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, cardID);
        return intent;
    }
}
