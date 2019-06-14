package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * A Fragment responsible for displaying the main screen. This is where most of the user interaction
 * will take place.
 *
 * @author Rick Myers
 */
public class GiftCardListFragment extends Fragment {

    // logging tag
    private static final String TAG = "GiftCardListFragment";

    private static final int REQUEST_DELETE = 0;
    private static final int REQUEST_ADD = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String DIALOG_DELETE = "DialogDelete";

    private RecyclerView mCardRecyclerView;
    private GiftCardAdapter mAdapter;
    private int mLastUpdatedIndex = -1;
    private GiftCardLedger mGiftCardLedger;
    private boolean mSubtitleVisible;
    private TextView mEmptyView;
    private Callbacks mCallbacks;

    //testing
    private List<GiftCard> mGiftCards;

    public interface Callbacks {
        void onGiftCardSelected(GiftCard card);
        void onGiftCardAdd();
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
     * On Fragment creation, enables Options Menu.
     *
     * @param savedInstanceState the Bundle used to host Fragment data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate");
        mGiftCardLedger = GiftCardLedger.get(getActivity());
        mGiftCards = mGiftCardLedger.getGiftCardList();
    }

    /**
     * Returns main Fragment view for the {@link GiftCardListFragment}.
     *
     * @param inflater           the layout inflater
     * @param container          the ViewGroup which contains this view
     * @param savedInstanceState the Bundle used to host Fragment data
     * @return the main view for {@link GiftCardListFragment}
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        mCardRecyclerView = view.findViewById(R.id.card_recycler_view);
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyView = view.findViewById(R.id.empty_view);

        // if two panes are used, the up arrow is not needed
        disableUpIfTwoPane();

        // If the Bundle is not empty, set subtitle option accordingly
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        mGiftCardLedger = GiftCardLedger.get(getActivity());
        mGiftCards = mGiftCardLedger.getGiftCardList();

    }

    /**
     * Updates UI on Activity resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        updateUI();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void disableUpIfTwoPane() {
        // Get which view group is inflated by the activity
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0);

        // todo add "double" string to resources
        if(viewGroup.getTag().toString().equalsIgnoreCase("double"))
        {
            // Get a support ActionBar corresponding to this toolbar
            ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
            // Disable the Up button
            ab.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * Saves instance state.
     *
     * @param outState Bundle used for saving
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    /**
     * Creates option menu.
     *
     * @param menu     the main menu
     * @param inflater the inflater for menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * Processes the selected {@link MenuItem} and returns true when processing has been completed.
     *
     * @param item the selected {@link MenuItem}
     * @return true after processing for selected item is completed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_card:
                mCallbacks.onGiftCardAdd();
                return true;
            case R.id.show_subtitle:
                // toggle the subtitle and recreate the options menu
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * Updates the subtitle based on the number of cards are in the list.
     */
    private void updateSubtitle() {
        int cardCount = mGiftCardLedger.getGiftCardList().size();

        // Card or Cards based on card count.
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, cardCount, cardCount);

        // if false, subtitle is not shown.
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    /**
     * Insures the UI is up to date. Responsible for setting up the recycler view adapter and
     * updating positions based on other activities.
     */
    public void updateUI() {
        if (mAdapter == null) {
            Log.d(TAG, "If - UpdateUI");
            mAdapter = new GiftCardAdapter(mGiftCards);
            mCardRecyclerView.setAdapter(mAdapter);

            // setup slide to delete
            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    FragmentManager manager = getFragmentManager();

                    // find which viewHolder was clicked
                    int clicked = viewHolder.getAdapterPosition();
                    // find the card that was clicked in the database
                    GiftCard card = mGiftCardLedger.getGiftCardList().get(clicked);

                    // Setting which card was swiped
                    mLastUpdatedIndex = clicked;

                    // Call the delete card dialog to confirm deletion
                    DeleteCardFragment dialog = DeleteCardFragment.newInstance(card.getId());
                    dialog.setTargetFragment(GiftCardListFragment.this, REQUEST_DELETE);
                    dialog.show(manager, DIALOG_DELETE);
                }
            }).attachToRecyclerView(mCardRecyclerView);

        } else {
            if (mLastUpdatedIndex > -1) {
                Log.d(TAG, "Else - if in UpdateUI");
                mAdapter.notifyItemChanged(mLastUpdatedIndex);
                mLastUpdatedIndex = -1;
            }
        }

        updateEmptyView();
        updateSubtitle();
    }

    /**
     * Checks if the gift card list is empty. If it is empty, displays the empty view. Otherwise,
     * displays the recycler view.
     */
    private void updateEmptyView() {
        if (mGiftCardLedger.getGiftCardList().isEmpty()) {
            mCardRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mCardRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Processes data after and Activity returns a result.
     *
     * @param requestCode the code used when the Activity was started
     * @param resultCode  the code used when the Activity finished
     * @param data        the intent that hosts the data of the finished Activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Request: " + requestCode + " Result: " + resultCode);

        // Deletes card if user requested to delete during delete dialog
        if (requestCode == REQUEST_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult - Delete");
                UUID id = (UUID) data.getSerializableExtra(DeleteCardFragment.EXTRA_DELETE);
                mGiftCardLedger.removeGiftCard(id);
                mAdapter.removeCard(mLastUpdatedIndex);
                updateEmptyView();
            } else if (resultCode == Activity.RESULT_CANCELED){
                updateUI();
            }
        }

        // Adds new card if user saved a new card in GiftCardAddActivity
        if (requestCode == REQUEST_ADD && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult - Add");

            // get added card from EXTRA
            UUID id = (UUID) data.getSerializableExtra(GiftCardAddFragment.EXTRA_ADD);
            // add card to ledger
            GiftCard card = mGiftCardLedger.getGiftCard(id);
            // add to adapter
            mAdapter.addCard(mGiftCards.size() - 1, card);
        }
    }

    public void addCard(int position, GiftCard card){
        mAdapter.addCard(position, card);
    }

    /**
     * A {@link RecyclerView.ViewHolder} for {@link GiftCard}.
     */
    private class GiftCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private TextView mBalanceTextView;
        private GiftCard mGiftCard;
        private ImageView mImageView;



        public GiftCardHolder(LayoutInflater inflater, ViewGroup parent, GiftCardAdapter testAdapter) {
            super(inflater.inflate(R.layout.cardview_item, parent, false)); //list_item_card, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView = itemView.findViewById(R.id.card_name);
            mBalanceTextView = itemView.findViewById(R.id.card_balance);
            mImageView = itemView.findViewById(R.id.dollar_image);
        }

        /**
         * Binds data from a {@link GiftCard} to the ViewHolder.
         *
         * @param card the gift card to be bound to ViewHolder
         */
        public void bind(GiftCard card) {
            mGiftCard = card;
            mNameTextView.setText(mGiftCard.getName());
            mBalanceTextView.setText(GiftCard.getFormattedBalance(mGiftCard.getBalance()));

            int test = card.getBalance().toBigInteger().intValue();
            if(test > 50){
                mImageView.setColorFilter(getResources().getColor(R.color.j_green));
            } else if(test >= 25 && test <= 50){
                mImageView.setColorFilter(getResources().getColor(R.color.j_yellow));
            } else if (test < 25)
                mImageView.setColorFilter(getResources().getColor(R.color.j_red));
        }

        @Override
        public void onClick(View v) {
            mLastUpdatedIndex = this.getAdapterPosition();
            mCallbacks.onGiftCardSelected(mGiftCard);
        }
    }

    /**
     * A {@link RecyclerView.Adapter} for {@link GiftCard}
     */
    private class GiftCardAdapter extends RecyclerView.Adapter<GiftCardHolder> {
        private List<GiftCard> mGiftCards;

        public GiftCardAdapter(List<GiftCard> cards) {
            mGiftCards = cards;
        }

        @NonNull
        @Override
        public GiftCardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutinflater = LayoutInflater.from(getActivity());
            return new GiftCardHolder(layoutinflater, viewGroup, this);
        }

        @Override
        public void onBindViewHolder(@NonNull GiftCardHolder giftCardHolder, int i) {
            GiftCard giftCard = mGiftCards.get(i);
            giftCardHolder.bind(giftCard);
        }

        @Override
        public int getItemCount() {
            return mGiftCards.size();
        }

        public void removeCard(int position){
            mGiftCards.remove(position);
            notifyItemRemoved(position);
        }

        public void addCard(int position, GiftCard card){
            mGiftCards.add(card);
            notifyItemInserted(position);
        }
    }


}
