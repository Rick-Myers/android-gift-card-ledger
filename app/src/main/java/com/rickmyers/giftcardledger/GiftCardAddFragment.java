package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;

/**
 * A Fragment responsible for gathering user input, creating a {@link GiftCard} and adding to the database.
 *
 * @author Rick Myers
 */
public class GiftCardAddFragment extends Fragment {

    // logging tag
    private static final String TAG = "GiftCardAddFragment";

    public static final String EXTRA_ADD = "com.rickmyers.giftcardledger.add";
    private EditText mName;
    private EditText mBalance;
    private FloatingActionButton mFab;

    /**
     * Returns a {@link View} which contains user input fields for creating a new {@link GiftCard}
     *
     * @param inflater           the layout inflater
     * @param container          the ViewGroup which contains this view
     * @param savedInstanceState the Bundle used to host Fragment data
     * @return an inflated Add card view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_card, container, false);

        mName = v.findViewById(R.id.card_name);
        mBalance = v.findViewById(R.id.card_balance);

        // todo perhaps a reference to activity isn't needed.
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // todo perhaps add a save button to the menu instead of using FAB?
        mFab = getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo validate this data!
                if (mBalance.getText().length() > 0 && mName.getText() != null) {
                    // create a new gift card with the data given by the user and add to ledger which inserts into database
                    GiftCard newCard = new GiftCard();
                    newCard.setBalance(new BigDecimal(mBalance.getText().toString()));
                    newCard.setName(mName.getText().toString());
                    GiftCardLedger.get(getActivity()).addCard(newCard);

                    // return intent to the calling activity with the results of the card add
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(EXTRA_ADD, newCard.getId());
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);

                    // end the activity
                    getActivity().finish();
                }
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
