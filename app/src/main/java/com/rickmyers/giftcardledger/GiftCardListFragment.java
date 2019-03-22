package com.rickmyers.giftcardledger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GiftCardListFragment extends Fragment {
    private RecyclerView mCardRecyclerView;
    private GiftCardAdapter mAdapter;
    private int mLastUpdatedIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_list, container, false);

        mCardRecyclerView = view.findViewById(R.id.card_recycler_view);
        mCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        GiftCardLedger giftCardLedger = GiftCardLedger.get(getActivity());
        List<GiftCard> giftCards = giftCardLedger.getGiftCardList();

        if (mAdapter == null){
            mAdapter = new GiftCardAdapter(giftCards);
            mCardRecyclerView.setAdapter(mAdapter);
        } else {
            if (mLastUpdatedIndex > -1){
                mAdapter.notifyItemChanged(mLastUpdatedIndex);
                mLastUpdatedIndex = -1;
            } else {
                mAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private class GiftCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNameTextView;
        private TextView mBalanceTextView;
        private GiftCard mGiftCard;
        private static final String TAG = "GiftCardHolder";

        public GiftCardHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_card, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView = itemView.findViewById(R.id.card_name);
            mBalanceTextView = itemView.findViewById(R.id.card_balance);
        }

        public void bind(GiftCard card) {
            mGiftCard = card;
            mNameTextView.setText(mGiftCard.getName());
            mBalanceTextView.setText(Float.toString(mGiftCard.getBalance()));
        }

        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(getActivity(), GiftCardEditActivity.class);
            Intent intent = GiftCardEditActivity.newIntent(getActivity(), mGiftCard.getId());
            mLastUpdatedIndex = this.getAdapterPosition();
            startActivity(intent);
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
            return new GiftCardHolder(layoutinflater, viewGroup);
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
    }
}
