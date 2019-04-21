package com.rickmyers.giftcardledger;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * An abstract {@link android.app.Activity} used to host a single fragment.
 *
 * @author Rick Myers
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * Returns fragment to be hosted by this Activity.
     *
     * @return fragment
     */
    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.fragment_main;
    }

    /**
     * Inflates fragment container and adds fragment.
     *
     * @param savedInstanceState the Bundle used to host Fragment data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
