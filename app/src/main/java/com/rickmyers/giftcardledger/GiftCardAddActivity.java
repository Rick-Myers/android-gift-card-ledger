package com.rickmyers.giftcardledger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * An Activity used to add {@link GiftCard}
 */
public class GiftCardAddActivity extends AppCompatActivity {
    // todo consider using SingleFragmentActivity
    /**
     * Setups on toolbar and actionbar. Adds {@link GiftCardAddFragment} to container if it doesn't exist.
     *
     * @param savedInstanceState the Bundle is used to host gift card id data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);//single_frame_layout);

        // find and set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        // load fragment into container if container is empty
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new GiftCardAddFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
