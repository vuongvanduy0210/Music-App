package com.vuongvanduy.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vuongvanduy.music.fragment.AllMusicFragment;
import com.vuongvanduy.music.fragment.FavouriteMusicFragment;
import com.vuongvanduy.music.fragment.HomeUiFragment;
import com.vuongvanduy.music.fragment.DeivceMusicFragment;


public class FragmentViewPager2Adapter extends FragmentStateAdapter {

    public FragmentViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeUiFragment();
            case 1:
                return new AllMusicFragment();
            case 2:
                return new FavouriteMusicFragment();
            default:
                return new DeivceMusicFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
