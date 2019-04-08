package com.rickmyers.giftcardledger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.UUID;

public class GiftCardEditFragment extends Fragment {

    private static final String ARG_CARD_ID = "card_id";

    private TextView mNameTextView;
    private EditText mBalanceEditText;
    private GiftCard mGiftCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID cardID = (UUID) getArguments().getSerializable(ARG_CARD_ID);
        mGiftCard = GiftCardLedger.get(getActivity()).getGiftCard(cardID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_card, container, false);

        mNameTextView = view.findViewById(R.id.card_name);
        mNameTextView.setText(mGiftCard.getName());


        mBalanceEditText = view.findViewById(R.id.card_balance_edit);
        mBalanceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    mGiftCard.setBalance(new BigDecimal(s.toString()));
                else
                    mGiftCard.setBalance(new BigDecimal(0));
            }
        });



        mBalanceEditText.setText(mGiftCard.getBalance().toString());

        return view;
    }

    public static GiftCardEditFragment newInstance(UUID cardID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD_ID, cardID);

        GiftCardEditFragment fragment = new GiftCardEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        // todo Does card need to be saved every time or only on update? Validate first?
        GiftCardLedger.get(getActivity()).updateGiftCard(mGiftCard);
    }
}
