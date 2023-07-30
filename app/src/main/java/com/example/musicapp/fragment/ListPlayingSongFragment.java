package com.example.musicapp.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.adapter.PlayingSongAdapter;
import com.example.musicapp.constant.Constant;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.databinding.FragmentListPlayingSongBinding;
import com.example.musicapp.service.MusicService;

public class ListPlayingSongFragment extends Fragment {

    private FragmentListPlayingSongBinding fragmentListSongPlayingBinding;
    private PlayingSongAdapter songPlayingAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatusListSongPlaying();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentListSongPlayingBinding = FragmentListPlayingSongBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        displayListSongPlaying();

        return fragmentListSongPlayingBinding.getRoot();
    }

    private void displayListSongPlaying() {
        if (getActivity() == null || MusicService.listSongPlaying == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentListSongPlayingBinding.rcvData.setLayoutManager(linearLayoutManager);

        songPlayingAdapter = new PlayingSongAdapter(MusicService.listSongPlaying, this::clickItemSongPlaying);
        fragmentListSongPlayingBinding.rcvData.setAdapter(songPlayingAdapter);

        updateStatusListSongPlaying();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateStatusListSongPlaying() {
        if (getActivity() == null || MusicService.listSongPlaying == null || MusicService.listSongPlaying.isEmpty()) {
            return;
        }
        for (int i = 0; i < MusicService.listSongPlaying.size(); i++) {
            MusicService.listSongPlaying.get(i).setPlaying(i == MusicService.songPosition);
        }
        songPlayingAdapter.notifyDataSetChanged();
    }

    private void clickItemSongPlaying(int position) {
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }
}