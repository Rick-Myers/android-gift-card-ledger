package com.rickmyers.giftcardledger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class GiftCardListFragment extends Fragment {
    private static final String TAG = "GiftCardListFragment";
    private static final int REQUEST_DELETE = 0;


    private RecyclerView mCardRecyclerView;
    private GiftCardAdapter mAdapter;
    private int mLastUpdatedIndex = -1;
    private GiftCardLedger mGiftCardLedger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        mCardRecyclerView = view.findViewById(R.id.card_recycler_view);
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GiftCardAddActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "I love you Janello!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        updateUI();

        return view;
    }

    private void updateUI() {
        mGiftCardLedger = GiftCardLedger.get(getActivity());
        List<GiftCard> giftCards = mGiftCardLedger.getGiftCardList();

        if (mAdapter == null){
            mAdapter = new GiftCardAdapter(giftCards);
            mCardRecyclerView.setAdapter(mAdapter);
        } else {
            if (mLastUpdatedIndex > -1){
                mAdapter.notifyItemChanged(mLastUpdatedIndex);
                mLastUpdatedIndex = -1;
                mAdapter.updateList();
                // todo This is lazy! Implement a way to update a list of items that were changed coming back to list activity.

                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        if (requestCode == REQUEST_DELETE){
            UUID id = (UUID) data.getSerializableExtra(DeleteCardFragment.EXTRA_DELETE);
            Log.d(TAG, "onActivityResult");
            mGiftCardLedger.removeGiftCard(id);
            mAdapter.updateList();
            mAdapter.notifyDataSetChanged();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class GiftCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mNameTextView;
        private TextView mBalanceTextView;
        private GiftCard mGiftCard;
        private GiftCardAdapter mCardAdapter;
        private static final String DIALOG_DELETE = "DialogDelete";
        private static final String TAG = "GiftCardHolder long";
        private static final int REQUEST_DELETE = 0;

        public GiftCardHolder(LayoutInflater inflater, ViewGroup parent, GiftCardAdapter testAdapter) {
            super(inflater.inflate(R.layout.list_item_card, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mNameTextView = itemView.findViewById(R.id.card_name);
            mBalanceTextView = itemView.findViewById(R.id.card_balance);
            mCardAdapter = testAdapter;
        }

        public void bind(GiftCard card) {
            mGiftCard = card;
            mNameTextView.setText(mGiftCard.getName());
            mBalanceTextView.setText(GiftCard.getFormattedBalance(mGiftCard.getBalance()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = GiftCardPagerActivity.newIntent(getActivity(), mGiftCard.getId());
            mLastUpdatedIndex = this.getAdapterPosition();
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(TAG, "Long Click");

            FragmentManager manager = getFragmentManager();
            DeleteCardFragment dialog = DeleteCardFragment.newInstance(mGiftCard.getId());
            dialog.setTargetFragment(GiftCardListFragment.this, REQUEST_DELETE);
            dialog.show(manager, DIALOG_DELETE);
            /*mGiftCardLedger.removeGiftCard(mGiftCard.getId());
            mCardAdapter.updateList();
            mCardAdapter.notifyDataSetChanged();*/
            return true;
        }
    }

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

        public void updateList(){
            mGiftCards = mGiftCardLedger.getGiftCardList();
        }
    }


}
