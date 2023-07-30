package com.example.musicapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.MyApplication;
import com.example.musicapp.R;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.databinding.FragmentHistoryBinding;
import com.example.musicapp.databinding.FragmentLyricBinding;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LyricFragment extends Fragment {

    private FragmentLyricBinding fragmentLyricBinding;
    private List<Song> mListSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLyricBinding = FragmentLyricBinding.inflate(inflater, container, false);

        getListSongs();
        displayLyric();

        return fragmentLyricBinding.getRoot();
    }

    private void displayLyric() {
        for (int i = 0; i < MusicService.listSongPlaying.size(); i++) {
            String lyric = MusicService.listSongPlaying.get(i).getLyric().toString();
            fragmentLyricBinding.tvLyric.setText(lyric);
        }
    }

    private void getListSongs() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    mListSong.add(0, song);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }
}