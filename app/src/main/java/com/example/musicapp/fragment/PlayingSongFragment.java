package com.example.musicapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import com.example.musicapp.R;
import com.example.musicapp.constant.Constant;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.databinding.FragmentPlayingSongBinding;
import com.example.musicapp.model.Song;
import com.example.musicapp.service.MusicService;
import com.example.musicapp.utils.AppUtil;
import com.example.musicapp.utils.GlideUtils;

import java.util.Timer;
import java.util.TimerTask;


public class PlayingSongFragment extends Fragment implements View.OnClickListener {

    private FragmentPlayingSongBinding fragmentPlayingSongBinding;
    private Timer timer;
    private int action;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPlayingSongBinding = FragmentPlayingSongBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        initControl();
        showInforSong();
        action = MusicService.action;
        handleMusicAction();

        return fragmentPlayingSongBinding.getRoot();
    }

    private void handleMusicAction() {
        //neu huy push noti thi quay lai man hinh chinh
        if (Constant.CANNEL_NOTIFICATION == action) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return;
        }
        switch (action) {
            case Constant.PREVIOUS:
            case Constant.NEXT:
                stopAnimationPlayMusic();
                showInforSong();
                break;

            case Constant.PLAY:
                showInforSong();
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic();
                }
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.PAUSE:
                stopAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.RESUME:
                startAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;

        }
    }

    private void showInforSong() {
        if (MusicService.listSongPlaying == null || MusicService.listSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.listSongPlaying.get(MusicService.songPosition);
        fragmentPlayingSongBinding.tvSongName.setText(currentSong.getName());
        fragmentPlayingSongBinding.tvArtist.setText(currentSong.getSinger());
        GlideUtils.loadUrl(currentSong.getImage(), fragmentPlayingSongBinding.imgSong);
    }

    private void startAnimationPlayMusic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fragmentPlayingSongBinding.imgSong.animate()
                        .rotationBy(360)
                        .withEndAction(this)
                        .setDuration(15000)
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
        };
        runnable.run();
    }

    private void stopAnimationPlayMusic() {
        fragmentPlayingSongBinding.imgSong.animate().cancel();
    }

    public void showSeekBar() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    if (MusicService.mediaPlayer == null) {
                        return;
                    }
                    fragmentPlayingSongBinding.tvTimeCurrent.setText(AppUtil.getTime(MusicService.mediaPlayer.getCurrentPosition()));
                    fragmentPlayingSongBinding.tvTimeMax.setText(AppUtil.getTime(MusicService.songLength));
                    fragmentPlayingSongBinding.seekbar.setMax(MusicService.songLength);
                    fragmentPlayingSongBinding.seekbar.setProgress(MusicService.mediaPlayer.getCurrentPosition());
                });
            }
        }, 0, 1000);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            fragmentPlayingSongBinding.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            fragmentPlayingSongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    private void showStatusLoopButton() {
        if (MusicService.repeatFlag) {
            fragmentPlayingSongBinding.imgLoop.setImageResource(R.drawable.ic_loop_black);
        } else {
            fragmentPlayingSongBinding.imgLoop.setImageResource(R.drawable.ic_loop_filled);
        }
        MusicService.repeatFlag = !MusicService.repeatFlag;
    }

    private void showStatusShuffleButton() {
        if (MusicService.shuffleFlag) {
            fragmentPlayingSongBinding.imgShuffle.setImageResource(R.drawable.ic_shuffle_black);
        } else {
            fragmentPlayingSongBinding.imgShuffle.setImageResource(R.drawable.ic_shuffle_gray);
        }
        MusicService.shuffleFlag = !MusicService.shuffleFlag;
    }

    private void initControl() {
        timer = new Timer();

        fragmentPlayingSongBinding.imgPrevious.setOnClickListener(this);
        fragmentPlayingSongBinding.imgPlay.setOnClickListener(this);
        fragmentPlayingSongBinding.imgNext.setOnClickListener(this);
        fragmentPlayingSongBinding.imgLoop.setOnClickListener(this);
        fragmentPlayingSongBinding.imgShuffle.setOnClickListener(this);

        fragmentPlayingSongBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;

            case R.id.img_loop:
                clickOnLoopButton();
                break;

            case R.id.img_shuffle:
                clickOnShuffleButton();
                break;
            default:
                break;
        }
    }

    private void clickOnPrevButton() {
        GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.songPosition);
    }

    private void clickOnNextButton() {
        GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.songPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(getActivity(), Constant.PAUSE, MusicService.songPosition);
        } else {
            GlobalFuntion.startMusicService(getActivity(), Constant.RESUME, MusicService.songPosition);
        }
    }

    private void clickOnLoopButton() {
        showStatusLoopButton();
    }

    private void clickOnShuffleButton() {
        showStatusShuffleButton();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        }
    }
}