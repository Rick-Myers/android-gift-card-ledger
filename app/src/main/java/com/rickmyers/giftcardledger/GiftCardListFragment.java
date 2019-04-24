package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        mGiftCardLedger = GiftCardLedger.get(getActivity());

        mCardRecyclerView = view.findViewById(R.id.card_recycler_view);
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mEmptyView = view.findViewById(R.id.empty_view);

        // Get which view group is inflated by the activity
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup)getActivity().findViewById(android.R.id.content)).getChildAt(0);

        if(viewGroup.getTag().toString() == "double")
        {
            Toolbar toolbar = viewGroup.findViewById(R.id.toolbar);
            // Get a support ActionBar corresponding to this toolbar
            ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(false);
        }

        // todo remove and use Options menu
        /*FloatingActionButton fab = view.findViewById(R.id.fab);
        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "I love you Janello!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        // If the Bundle is not empty, set subtitle option accordingly
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
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
                /*// start GiftCardAddActivity and wait for result
                Intent intent = new Intent(getActivity(), GiftCardAddActivity.class);
                startActivityForResult(intent, REQUEST_ADD);*/

                //testing callbacks
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
        mGiftCardLedger = GiftCardLedger.get(getActivity());
        List<GiftCard> giftCards = mGiftCardLedger.getGiftCardList();

        if (mAdapter == null) {
            mAdapter = new GiftCardAdapter(giftCards);
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
                    // Call the delete card dialog to give the user option to delete the card
                    DeleteCardFragment dialog = DeleteCardFragment.newInstance(card.getId());
                    dialog.setTargetFragment(GiftCardListFragment.this, REQUEST_DELETE);
                    dialog.show(manager, DIALOG_DELETE);
                }
            }).attachToRecyclerView(mCardRecyclerView);

        } else {
            // todo This is lazy! Implement a way to update a list of items that were changed coming back to list activity.
            if (mLastUpdatedIndex > -1) {
                mAdapter.notifyItemChanged(mLastUpdatedIndex);
                mLastUpdatedIndex = -1;
                mAdapter.updateList(giftCards);
                // todo This is lazy!
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyItemChanged(0);
                mAdapter.updateList(giftCards);
                mAdapter.notifyDataSetChanged();
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
        Log.d(TAG, "Request: " + requestCode + "Result: " + resultCode);

        // Deletes card if user requested to delete during delete dialog
        if (requestCode == REQUEST_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                UUID id = (UUID) data.getSerializableExtra(DeleteCardFragment.EXTRA_DELETE);
                Log.d(TAG, "onActivityResult - Delete");
                mGiftCardLedger.removeGiftCard(id);
                List<GiftCard> giftCards = mGiftCardLedger.getGiftCardList();
                // todo only update the card that was added
                mAdapter.updateList(giftCards);
                mAdapter.notifyDataSetChanged();
                updateUI();
            }
        }

        // Adds new card if user saved a card in GiftCardAddActivity
        if (requestCode == REQUEST_ADD) {
            List<GiftCard> giftCards = mGiftCardLedger.getGiftCardList();
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult - Add");

                // todo only update the card that was added
                UUID id = (UUID) data.getSerializableExtra(GiftCardAddFragment.EXTRA_ADD);
                mAdapter.updateList(giftCards);
                mAdapter.notifyDataSetChanged();
            }

        }

        //testing update when card not removed on swipe
        updateUI();

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Updates UI on Activity resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * A {@link RecyclerView.ViewHolder} for {@link GiftCard}.
     */
    private class GiftCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private TextView mBalanceTextView;
        private GiftCard mGiftCard;

        public GiftCardHolder(LayoutInflater inflater, ViewGroup parent, GiftCardAdapter testAdapter) {
            super(inflater.inflate(R.layout.list_item_card, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView = itemView.findViewById(R.id.card_name);
            mBalanceTextView = itemView.findViewById(R.id.card_balance);
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
        }

        @Override
        public void onClick(View v) {
            // starts intent to edit the card that was clicked. also sets this position as the last
            // updated
            //Intent intent = GiftCardPagerActivity.newIntent(getActivity(), mGiftCard.getId());
            mLastUpdatedIndex = this.getAdapterPosition();
            //startActivity(intent);
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

        public void updateList(List<GiftCard> cards) {
            mGiftCards = cards;
        }
    }


}
