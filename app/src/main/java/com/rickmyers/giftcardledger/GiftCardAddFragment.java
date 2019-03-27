package com.rickmyers.giftcardledger;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;

/**
 * todo Validate input! Be careful at the moment, input is not validated and will crash the app!
 */

public class GiftCardAddFragment extends Fragment {

    private GiftCard mGiftCard;
    private static final String TAG = "GiftCardAddFragment";
    private EditText mName;
    private EditText mBalance;
    private EditText mNumber;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGiftCard = new GiftCard();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_card, container, false);

        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "I love you Janello!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mName = v.findViewById(R.id.card_name);
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGiftCard.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBalance = v.findViewById(R.id.card_balance);
        mBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGiftCard.setBalance(new BigDecimal(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNumber = v.findViewById(R.id.card_number);
        mNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGiftCard.setNumber(Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
    }
}
