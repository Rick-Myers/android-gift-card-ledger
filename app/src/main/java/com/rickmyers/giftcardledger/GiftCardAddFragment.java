package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button mSaveButton;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onGiftCardAdded(GiftCard card);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Calls back to hosting activity to handle adding a new card.
     * @param card a new {@link GiftCard}
     */
    private void addGiftCard(GiftCard card){
        mCallbacks.onGiftCardAdded(card);
    }


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
        mSaveButton = v.findViewById(R.id.save_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBalance.getText().length() > 0 && mName.getText() != null) {
                    GiftCard newCard = new GiftCard(mName.getText().toString(), new BigDecimal(mBalance.getText().toString()));
                    addGiftCard(newCard);
                }
            }
        });

        // Up button is not used if tablet or larger screens are active.
        disableUpIfTwoPane();

        return v;
    }

    /**
     * Disables the "Up" button for larger screens. On larger screens, a two pane window is used
     * and the card list is always visible. There is no need to go "Up".
     */
    private void disableUpIfTwoPane() {
        // Get which view group is inflated by the activity
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

       // todo add "double" string to resources
        if(viewGroup.getTag().toString().equalsIgnoreCase("double")) {
            // Disable the Up button
            ab.setDisplayHomeAsUpEnabled(false);
        } else {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
        }
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
