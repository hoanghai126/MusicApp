package com.example.musicapp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.musicapp.MyApplication;
import com.example.musicapp.R;
import com.example.musicapp.activity.MainActivity;
import com.example.musicapp.activity.PlayMusicActivity;
import com.example.musicapp.adapter.BannerSongAdapter;
import com.example.musicapp.adapter.GridSongAdapter;
import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.constant.Constant;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.databinding.FragmentHomeBinding;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;
import com.example.musicapp.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding fragmentHomeBinding;
    private List<Song> songList;
    private List<Song> listSongBanner;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (listSongBanner == null || listSongBanner.isEmpty()) {
                return;
            }
            if (fragmentHomeBinding.viewpager2.getCurrentItem() == listSongBanner.size() - 1) {
                fragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            fragmentHomeBinding.viewpager2.setCurrentItem(fragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        getListSongFromFirebase("");
        initListener();

        return fragmentHomeBinding.getRoot();
    }

    private void initListener() {
        fragmentHomeBinding.edtSearchSong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (songList != null) songList.clear();
                    getListSongFromFirebase("");
                }
            }
        });

        fragmentHomeBinding.imgSearch.setOnClickListener(view -> searchSong());

        //true tra ve bai hat dc search
        fragmentHomeBinding.edtSearchSong.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        fragmentHomeBinding.layoutViewAllPopular.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openPopularSongsScreen();
            }
        });

        fragmentHomeBinding.layoutViewAllNew.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openNewSongsScreen();
            }
        });
    }

    private void getListSongFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
                songList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }

                    if (StringUtil.isEmpty(key)) {
                        songList.add(0, song);
                    }
                    else {
                        // tim kiem theo ten bai hat
                        if (GlobalFuntion.getTextSearch(song.getName()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                            songList.add(0, song);
                        }
                        // tim kiem theo ten ca si
                        if (GlobalFuntion.getTextSearch(song.getSinger()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                            songList.add(0, song);
                        }
                    }
                }
                displayListBannerSongs();
                displayListPopularSongs();
                displayListNewSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListBannerSongs() {
        BannerSongAdapter bannerSongAdapter = new BannerSongAdapter(getListBannerSongs(), this::goToSongDetail);
        fragmentHomeBinding.viewpager2.setAdapter(bannerSongAdapter);
        fragmentHomeBinding.indicator3.setViewPager(fragmentHomeBinding.viewpager2);

        fragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private List<Song> getListBannerSongs() {
        if (listSongBanner != null) {
            listSongBanner.clear();
        } else {
            listSongBanner = new ArrayList<>();
        }
        if (songList == null || songList.isEmpty()) {
            return listSongBanner;
        }
        for (Song song : songList) {
            if (song.isFeatured() && listSongBanner.size() < Constant.MAX_COUNT_BANNER) {
                listSongBanner.add(song);
            }
        }
        return listSongBanner;
    }

    private void displayListPopularSongs() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        fragmentHomeBinding.rcvPopularSongs.setLayoutManager(gridLayoutManager);

        GridSongAdapter songGridAdapter = new GridSongAdapter(getListPopularSongs(), this::goToSongDetail);
        fragmentHomeBinding.rcvPopularSongs.setAdapter(songGridAdapter);
    }

    private List<Song> getListPopularSongs() {
        List<Song> list = new ArrayList<>();
        if (songList == null || songList.isEmpty()) {
            return list;
        }
        List<Song> allSongs = new ArrayList<>(songList);
        Collections.sort(allSongs, (song1, song2) -> song2.getCount() - song1.getCount());
        for (Song song : allSongs) {
            if (list.size() < Constant.MAX_COUNT_POPULAR) {
                list.add(song);
            }
        }
        return list;
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentHomeBinding.rcvNewSongs.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(getListNewSongs(), this::goToSongDetail);
        fragmentHomeBinding.rcvNewSongs.setAdapter(songAdapter);
    }

    private List<Song> getListNewSongs() {
        List<Song> list = new ArrayList<>();
        if (songList == null || songList.isEmpty()) {
            return list;
        }
        for (Song song : songList) {
            if (song.isLatest() && list.size() < Constant.MAX_COUNT_LATEST) {
                list.add(song);
            }
        }
        return list;
    }

    private void searchSong() {
        String strKey = fragmentHomeBinding.edtSearchSong.getText().toString().trim();
        if (songList != null) songList.clear();
        getListSongFromFirebase(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.listSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

}