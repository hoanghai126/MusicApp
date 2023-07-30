package com.example.musicapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapp.MyApplication;
import com.example.musicapp.R;
import com.example.musicapp.activity.MainActivity;
import com.example.musicapp.activity.PlayMusicActivity;
import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.constant.Constant;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.databinding.FragmentFeaturedSongsBinding;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FeaturedSongsFragment extends Fragment {

    private FragmentFeaturedSongsBinding fragmentFeaturedSongsBinding;

    private List<Song> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentFeaturedSongsBinding = FragmentFeaturedSongsBinding.inflate(inflater, container, false);

        getListFeaturedSongs();
        initListener();

        return fragmentFeaturedSongsBinding.getRoot();
    }

    private void getListFeaturedSongs() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    if (song.isFeatured()) {
                        songList.add(0, song);
                    }
                }
                displayListFeaturedSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListFeaturedSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentFeaturedSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(songList, this::goToSongDetail);
        fragmentFeaturedSongsBinding.rcvData.setAdapter(songAdapter);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.listSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    private void initListener(){
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null || activity.getActivityMainBinding() == null) {
            return;
        }
        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.listSongPlaying.addAll(songList);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
    }

}