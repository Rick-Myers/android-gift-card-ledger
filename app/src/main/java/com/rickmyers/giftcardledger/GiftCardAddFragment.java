package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * todo Validate input! Be careful at the moment, input is not validated and will crash the app!
 */

public class GiftCardAddFragment extends Fragment {

    private static final String ARG_CARD_ID = "card_id";
    public static final String EXTRA_ADD = "com.rickmyers.giftcardledger.delete";
    private GiftCard mGiftCard;
    private static final String TAG = "GiftCardAddFragment";
    private EditText mName;
    private EditText mBalance;
    private EditText mNumber;
    private FloatingActionButton mFab;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //UUID cardID = (UUID) getArguments().getSerializable(ARG_CARD_ID);
        //mGiftCard = GiftCardLedger.get(getActivity()).getGiftCard(cardID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_card, container, false);




        mName = v.findViewById(R.id.card_name);
        mBalance = v.findViewById(R.id.card_balance);

        mFab = getActivity().findViewById(R.id.fab);//v.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBalance.getText().length() > 0 && mName.getText() != null) {
                    GiftCard newCard = new GiftCard();
                    GiftCardLedger.get(getActivity()).addCard(newCard);
                    newCard.setBalance(new BigDecimal(mBalance.getText().toString()));
                    newCard.setName(mName.getText().toString());


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(EXTRA_ADD, newCard.getId());
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);

                    //sendResult(getActivity().RESULT_OK, mGiftCard.getId());
                    getActivity().finish();
                }
            }
        });



        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //mGiftCard.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mBalance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //mGiftCard.setBalance(new BigDecimal(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

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

    public static GiftCardAddFragment newInstance(UUID cardID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CARD_ID, cardID);

        GiftCardAddFragment fragment = new GiftCardAddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, UUID id){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADD, id);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
