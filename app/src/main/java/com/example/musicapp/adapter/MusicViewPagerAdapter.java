package com.example.musicapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapp.fragment.ListPlayingSongFragment;
import com.example.musicapp.fragment.LyricFragment;
import com.example.musicapp.fragment.PlayingSongFragment;

public class MusicViewPagerAdapter extends FragmentStateAdapter {

    public MusicViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ListPlayingSongFragment();
        } else if (position == 1) {
            return new PlayingSongFragment();
        } else
        return new LyricFragment();

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
