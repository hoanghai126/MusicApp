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
import com.example.musicapp.databinding.FragmentAllSongsBinding;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AllSongsFragment extends Fragment {

    private FragmentAllSongsBinding fragmentAllSongsBinding;
    private List<Song> songList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentAllSongsBinding = FragmentAllSongsBinding.inflate(inflater, container, false);

        getListSongs();
        initListener();

        return fragmentAllSongsBinding.getRoot();
    }

    private void getListSongs(){
        if(getActivity() == null){
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    songList.add(0, song);
                }
                displayListAllSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentAllSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(songList, this::goToSongDetail);
        fragmentAllSongsBinding.rcvData.setAdapter(songAdapter);
    }

    //chuyen den man hinh play song
    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.listSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    private void initListener() {
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