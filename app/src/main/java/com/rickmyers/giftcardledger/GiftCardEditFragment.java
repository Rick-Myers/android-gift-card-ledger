package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rickmyers.giftcardledger.utilities.PictureUtils;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
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
    /*private static final int REQUEST_PHOTO = 0;
    private static final String DIALOG_IMAGE = "DialogImage";
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Point mPhotoViewDimensions;*/

    private TextView mNameTextView;
    private TextView mBalanceTextView;
    private EditText mBalanceEditText;
    private GiftCard mGiftCard;

    private Callbacks mCallbacks;
    private Button mAddDeductButton;
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
        /*mPhotoFile = GiftCardLedger.get(getActivity()).getPhotoFile(mGiftCard);*/
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

        PackageManager packageManager = getActivity().getPackageManager();

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
                    }
                }
             }
        });


        /*setupPhotoView(view, packageManager);*/

        return view;
    }

    private void clearEditText() {
        mBalanceEditText.getText().clear();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mBalanceEditText.getWindowToken(), 0);
    }

    private void setupHistoryRecycler() {

        if (mAdapter == null) {
            mAdapter = new HistoryAdapter(mGiftCard.getHistory());
            mCardRecyclerView.setAdapter(mAdapter);
        }
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /*private void setupPhotoView(View view, PackageManager packageManager) {
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mPhotoView = view.findViewById(R.id.card_picture);
        mPhotoButton = view.findViewById(R.id.button_take_picture);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.rickmyers.giftcardledger.fileprovider", mPhotoFile);

            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPhotoViewDimensions = new Point();
                mPhotoViewDimensions.set(mPhotoView.getWidth(), mPhotoView.getHeight());

                updatePhotoView();
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(mPhotoFile!= null && mPhotoFile.exists())
                {
                    FragmentManager fragmentManager = getFragmentManager();

                    ImagePreviewFragment.newInstance(mPhotoFile).show(fragmentManager, DIALOG_IMAGE);
                }
            }
        });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        /*if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.rickmyers.giftcardledger.fileprovider", mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }*/
    }

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
        // Update the card with given data if possible
        // todo validate before updating
        //GiftCardLedger.get(getActivity()).updateGiftCard(mGiftCard);
    }

    /*private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.card_photo_no_image_description));
        } else {
            Bitmap bitmap = (mPhotoViewDimensions == null) ? PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity()) : PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoViewDimensions.x, mPhotoViewDimensions.y);
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.card_photo_image_description));
        }
    }*/


    private class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView mDateTextView;
        private TextView mBalanceTextView;
        private List<String> mHistory;

        public HistoryHolder(LayoutInflater inflater, ViewGroup parent, HistoryAdapter testAdapter) {
            super(inflater.inflate(R.layout.list_item_card, parent, false));

            mDateTextView = itemView.findViewById(R.id.card_date);
            mBalanceTextView = itemView.findViewById(R.id.card_balance);
        }

        public void bind(List<String> history) {
            mHistory = history;
            mDateTextView.setText(mHistory.get(0));
            BigDecimal balance = new BigDecimal(mHistory.get(1));
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

        /*public void updateList(List<GiftCard> cards) {
            mGiftCards = cards;
        }*/
    }
}
