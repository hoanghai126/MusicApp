package com.example.musicapp.activity;

import android.os.Bundle;
import android.view.View;

import com.example.musicapp.R;
import com.example.musicapp.adapter.MusicViewPagerAdapter;
import com.example.musicapp.databinding.ActivityPlayMusicBinding;

public class PlayMusicActivity extends BaseActivity {

    private ActivityPlayMusicBinding activityPlayMusicBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlayMusicBinding = ActivityPlayMusicBinding.inflate(getLayoutInflater());
        setContentView(activityPlayMusicBinding.getRoot());

        initToolbar();
        initUI();
    }

    private void initToolbar() {
        activityPlayMusicBinding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        activityPlayMusicBinding.toolbar.tvTitle.setText(R.string.music_player);
        activityPlayMusicBinding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        activityPlayMusicBinding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initUI() {
        MusicViewPagerAdapter musicViewPagerAdapter = new MusicViewPagerAdapter(this);
        activityPlayMusicBinding.viewpager2.setAdapter(musicViewPagerAdapter);
        activityPlayMusicBinding.indicator3.setViewPager(activityPlayMusicBinding.viewpager2);
        activityPlayMusicBinding.viewpager2.setCurrentItem(1);
    }
}
