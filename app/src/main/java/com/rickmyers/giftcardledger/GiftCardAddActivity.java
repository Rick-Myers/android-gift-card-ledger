package com.rickmyers.giftcardledger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.UUID;


public class GiftCardAddActivity extends AppCompatActivity {

    private static final String EXTRA_CARD_ID = "com.rickmyers.giftcardledger.card_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);//single_frame_layout);

        UUID cardID = (UUID) getIntent().getSerializableExtra(EXTRA_CARD_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = GiftCardAddFragment.newInstance(cardID);
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    public static Intent newIntent(Context packageContext, UUID cardID){
        Intent intent = new Intent(packageContext, GiftCardAddActivity.class);
        intent.putExtra(EXTRA_CARD_ID, cardID);
        return intent;
    }

}
