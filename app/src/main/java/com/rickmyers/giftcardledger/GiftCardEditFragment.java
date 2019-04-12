package com.rickmyers.giftcardledger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * A Fragment responsible for allowing the user a way to edit/manage a {@link GiftCard}.
 *
 * @author Rick Myers
 */
public class GiftCardEditFragment extends Fragment {

    private static final String ARG_CARD_ID = "card_id";

    private TextView mNameTextView;
    private EditText mBalanceEditText;
    private GiftCard mGiftCard;

    /**
     * Retrieves {@link GiftCard} data from model when hosting Activity is created.
     *
     * @param savedInstanceState the Bundle used to host Fragment data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID cardID = (UUID) getArguments().getSerializable(ARG_CARD_ID);
        mGiftCard = GiftCardLedger.get(getActivity()).getGiftCard(cardID);
    }

    /**
     * Returns a {@link View} which contains user input fields for editing an existing {@link GiftCard}.
     *
     * @param inflater           the layout inflater
     * @param container          the ViewGroup which contains this view
     * @param savedInstanceState the Bundle used to host Fragment data
     * @return an inflated Edit card view
     */
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
                // todo provide better validation for input, also use a try/catch
                if (s.length() > 0)
                    mGiftCard.setBalance(new BigDecimal(s.toString()));
                else
                    mGiftCard.setBalance(new BigDecimal(0));
            }
        });

        mBalanceEditText.setText(mGiftCard.getBalance().toString());

        return view;
    }

    /**
     * Returns a new {@link GiftCardEditFragment} with a Bundle that includes the gift card's UUID.
     *
     * @param cardID the gift card's {@link UUID}
     * @return a new {@link GiftCardEditFragment}
     */
    public static GiftCardEditFragment newInstance(UUID cardID) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD_ID, cardID);

        GiftCardEditFragment fragment = new GiftCardEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Update {@link GiftCard} data in database if Activity is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        // Update the card with given data if possible
        // todo validate before updating
        GiftCardLedger.get(getActivity()).updateGiftCard(mGiftCard);
    }
}
