package com.rickmyers.giftcardledger.utilities;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.rickmyers.giftcardledger.GiftCard;
import com.rickmyers.giftcardledger.GiftCardEditFragment;

import java.util.List;

public class CustomFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    SparseArray<Fragment> mRegisteredFragments = new SparseArray<>();
    private List<GiftCard> mGiftCards;

    public CustomFragmentStatePagerAdapter(FragmentManager fm, List<GiftCard> cards) {
        super(fm);
        mGiftCards = cards;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        GiftCard card = mGiftCards.get(position);
        return GiftCardEditFragment.newInstance(card.getId());
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mGiftCards.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        mRegisteredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mRegisteredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return mRegisteredFragments.get(position);
    }
}
