package com.rickmyers.giftcardledger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

public class GiftCardEditFragment extends Fragment {
    private TextView mNameTextView;
    private TextView mBalanceTextView;
    private GiftCard mGiftCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID cardID = (UUID) getActivity().getIntent().getSerializableExtra(GiftCardEditActivity.EXTRA_CARD_ID);
        mGiftCard = GiftCardLedger.get(getActivity()).getGiftCard(cardID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_card, container, false);

        mNameTextView = view.findViewById(R.id.card_name);
        mBalanceTextView = view.findViewById(R.id.card_balance);

        mNameTextView.setText(mGiftCard.getName());
        mBalanceTextView.setText(Float.toString(mGiftCard.getBalance()));

        return view;
    }


}
