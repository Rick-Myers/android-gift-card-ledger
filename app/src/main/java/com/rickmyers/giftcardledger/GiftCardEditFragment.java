package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * A Fragment responsible for allowing the user a way to edit/manage a {@link GiftCard}.
 *
 * @author Rick Myers
 */
public class GiftCardEditFragment extends Fragment {

    // logging tag
    private static final String TAG = "GiftCardEditFragment";

    private static final String ARG_CARD_ID = "card_id";
    private TextView mNameTextView;
    private TextView mBalanceTextView;
    private EditText mBalanceEditText;
    private GiftCard mGiftCard;
    private Callbacks mCallbacks;
    private Button mAddDeductButton;

    private Button mBackgroundColor;
    private Button mSymbolColor;
    private Button mFontColor;

    private RadioButton mAddRadioButton;
    private RadioButton mSubtractRadioButton;
    private RecyclerView mCardRecyclerView;
    private HistoryAdapter mAdapter;

    public interface Callbacks{
        void onGiftCardUpdated(GiftCard card);
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

        mCardRecyclerView = view.findViewById(R.id.history_recycler_view);
        setupHistoryRecycler();

        mNameTextView = view.findViewById(R.id.card_name);
        mNameTextView.setText(mGiftCard.getName());

        mBalanceTextView = view.findViewById(R.id.card_balance);
        mBalanceTextView.setText(GiftCard.getFormattedBalance(mGiftCard.getBalance()));

        mBalanceEditText = view.findViewById(R.id.card_balance_edit);

        mAddRadioButton = view.findViewById(R.id.add_radio_button);
        mAddRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddDeductButton.setText(R.string.add);
            }
        });

        mSubtractRadioButton = view.findViewById(R.id.subtract_radio_button);
        mSubtractRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddDeductButton.setText(R.string.deduct);
            }
        });

        setupColors(view);


        mAddDeductButton = view.findViewById(R.id.button_update_balance);
        mAddDeductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence s = mBalanceEditText.getText();
                if (s.length() <= 0){ }
                else {
                    BigDecimalValidator validator = CurrencyValidator.getInstance();
                    BigDecimal amount = validator.validate(s.toString());
                    Log.d(TAG, amount.toString());

                    if (amount == null)
                        clearEditText();
                    else {
                        if (mSubtractRadioButton.isChecked()){
                            mGiftCard.subtractFromBalance(amount);
                        } else {
                            mGiftCard.addToBalance(amount);
                        }

                        GiftCardEditFragment.this.clearEditText();
                        Log.d(TAG, Integer.toString(mAdapter.getItemCount()));
                        clearEditText();
                        updateGiftCard();
                        showUndoSnackbar();
                    }
                }

             }
        });

        return view;
    }

    private void setupColors(View view) {




        mBackgroundColor = view.findViewById(R.id.button_background_color);
        mBackgroundColor.setBackgroundColor(mGiftCard.getBackgroundColor());
        mBackgroundColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setColor(mGiftCard.getBackgroundColor()).setDialogId(GiftCardPagerActivity.DIALOG_ID_BACKGROUND).show(getActivity());
            }
        });

        mSymbolColor = view.findViewById(R.id.button_symbol_color);
        mSymbolColor.setBackgroundColor(mGiftCard.getSymbolColor());
        mSymbolColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setColor(mGiftCard.getSymbolColor()).setDialogId(GiftCardPagerActivity.DIALOG_ID_SYMBOL).show(getActivity());
            }
        });

        mFontColor = view.findViewById(R.id.button_font_color);
        mFontColor.setBackgroundColor(mGiftCard.getFontColor());
        mFontColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setColor(mGiftCard.getFontColor()).setDialogId(GiftCardPagerActivity.DIALOG_ID_FONT).show(getActivity());
            }
        });
    }

    public void changeColors(int color, int dialogId){
        switch (dialogId){
            case GiftCardPagerActivity.DIALOG_ID_BACKGROUND:
                mBackgroundColor.setBackgroundColor(color);
                break;
            case GiftCardPagerActivity.DIALOG_ID_SYMBOL:
                mSymbolColor.setBackgroundColor(color);
                break;
            case GiftCardPagerActivity.DIALOG_ID_FONT:
                mFontColor.setBackgroundColor(color);
                break;
        }




    }

    private void showUndoSnackbar(){
        View view = getActivity().findViewById(R.id.coordinatorLayout);
        Snackbar snackBar = Snackbar.make(view, R.string.undo_last, Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiftCardLedger.get(getActivity()).deleteLatestHistoryEntry(mGiftCard);
                mAdapter.undoAdd();
                mBalanceTextView.setText(GiftCard.getFormattedBalance(mGiftCard.getBalance()));

            }
        });
        snackBar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);


            }
        });
        snackBar.setDuration(6000);
        snackBar.show();
    }

    /**
     * Clears the test in the edit text view while also insuring that the soft keyboard is hidden.
     */
    private void clearEditText() {
        mBalanceEditText.getText().clear();
        //hide soft keyboard after text is cleared
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mBalanceEditText.getWindowToken(), 0);
    }

    /**
     * Checks to see if the history recycler adapter exists. If it does not, one is created.
     */
    private void setupHistoryRecycler() {

        if (mAdapter == null) {
            mAdapter = new HistoryAdapter(mGiftCard.getHistory());
            mCardRecyclerView.setAdapter(mAdapter);
        }
        // todo Does this need to be outside of the flow control?
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
    }

    /**
     * Updates gift card value in the ledger, card list, and text view when a transaction
     * is completed.
     */
    private void updateGiftCard() {
        GiftCardLedger.get(getActivity()).updateGiftCard(mGiftCard);
        mCallbacks.onGiftCardUpdated(mGiftCard);
        mBalanceTextView.setText(GiftCard.getFormattedBalance(mGiftCard.getBalance()));
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
     }

    private class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView mDateTextView;
        private TextView mTransacTextView;
        private TextView mBalanceTextView;
        private List<String> mHistory;

        public HistoryHolder(LayoutInflater inflater, ViewGroup parent, HistoryAdapter testAdapter) {
            super(inflater.inflate(R.layout.list_history_card, parent, false));

            mDateTextView = itemView.findViewById(R.id.card_date);
            mTransacTextView = itemView.findViewById(R.id.card_transac);
            mBalanceTextView = itemView.findViewById(R.id.card_balance);
        }

        public void bind(List<String> history) {
            mHistory = history;
            // set date
            mDateTextView.setText(mHistory.get(0));
            // set transaction
            String transac = mHistory.get(1);
            char neg = '-';
            mTransacTextView.setText(transac);
            if (transac.charAt(0) == neg){
                mTransacTextView.setTextColor(getResources().getColor(R.color.j_red));
            } else {
                mTransacTextView.setTextColor(getResources().getColor(R.color.j_green));
            }
            // set balance
            BigDecimal balance = new BigDecimal(mHistory.get(2));
            mBalanceTextView.setText(GiftCard.getFormattedBalance(balance));
        }

    }

    /**
     * A {@link RecyclerView.Adapter} for {@link GiftCard}
     */
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {
        private List<List<String>> mHistory;

        public HistoryAdapter(List<List<String>> history) {
            mHistory = history;
        }

        @NonNull
        @Override
        public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
            return new HistoryHolder(layoutinflater, viewGroup, this);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i) {
            List<String> history = mHistory.get(i);
            historyHolder.bind(history);
        }

        @Override
        public int getItemCount() {
            return mHistory.size();
        }

        public void undoAdd(){
            int mRecentlyAddedItemPosition = mHistory.size() - 1;
            mHistory.remove(mRecentlyAddedItemPosition);
            this.notifyItemRemoved(mRecentlyAddedItemPosition);
        }

    }
}
