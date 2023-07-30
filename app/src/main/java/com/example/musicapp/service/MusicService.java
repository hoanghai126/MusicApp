package com.example.musicapp.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.musicapp.MyApplication;
import com.example.musicapp.R;
import com.example.musicapp.activity.MainActivity;
import com.example.musicapp.constant.Constant;
import com.example.musicapp.constant.GlobalFuntion;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static boolean isPlaying;
    public static boolean repeatFlag;
    public static boolean shuffleFlag;

    public static List<Song> listSongPlaying;
    public static int songPosition;
    public static MediaPlayer mediaPlayer;
    public static int songLength;
    public static int action = -1;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constant.MUSIC_ACTION)) {
                action = bundle.getInt(Constant.MUSIC_ACTION);
            }
            if (bundle.containsKey(Constant.SONG_POSITION)) {
                songPosition = bundle.getInt(Constant.SONG_POSITION);
            }

            handleActionMusic(action);
        }

        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case Constant.PLAY:
                playSong();
                break;

            case Constant.PAUSE:
                pauseSong();
                break;

            case Constant.NEXT:
                nextSong();
                break;

            case Constant.PREVIOUS:
                prevSong();
                break;

            case Constant.RESUME:
                resumeSong();
                break;

            case Constant.CANNEL_NOTIFICATION:
                cannelNotification();
                break;

            default:
                break;
        }
    }

    private void playSong() {
        String songUrl = listSongPlaying.get(songPosition).getUrl();
        if (!StringUtil.isEmpty(songUrl)) {
            playMediaPlayer(songUrl);
        }
    }

    private void playShuffleSong() {
        Collections.shuffle(listSongPlaying);
        String songUrl = listSongPlaying.get(songPosition).getUrl();
        if (!StringUtil.isEmpty(songUrl)) {
            playMediaPlayer(songUrl);
        }
    }

    public void playMediaPlayer(String songUrl) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.prepareAsync();
            initControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseSong() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            sendMusicPushNotification();
            sendBroadcastChangeListener();
        }
    }

    private void nextSong() {
        if (listSongPlaying.size() > 1 && songPosition < listSongPlaying.size() - 1) {
            songPosition++;
        } else {
            songPosition = 0;
        }
        sendMusicPushNotification();
        sendBroadcastChangeListener();
        playSong();
    }

    private void prevSong() {
        if (listSongPlaying.size() > 1) {
            if (songPosition > 0) {
                songPosition--;
            } else {
                //di chuyen den bai hat cuoi cung trong list
                songPosition = listSongPlaying.size() - 1;
            }
        } else {
            //di chuyen den bai hat dau tien
            songPosition = 0;
        }
        sendMusicPushNotification();
        sendBroadcastChangeListener();
        playSong();
    }

    private void resumeSong() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            sendMusicPushNotification();
            sendBroadcastChangeListener();
        }
    }

    private void cannelNotification() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.cancelAll();
        sendBroadcastChangeListener();
        stopSelf();
    }

    private void sendBroadcastChangeListener() {
        Intent intent = new Intent(Constant.CHANGE_LISTENER);
        intent.putExtra(Constant.MUSIC_ACTION, action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendMusicPushNotification() {
        Song song = listSongPlaying.get(songPosition);

        int pendingFlag;
        pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingFlag);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_push_notification_music);
        remoteViews.setTextViewText(R.id.tv_song_name, song.getName());
        remoteViews.setTextViewText(R.id.tv_artist, song.getSinger());

        // Set listener
        remoteViews.setOnClickPendingIntent(R.id.img_previous, GlobalFuntion.openMusicReceiver(this, Constant.PREVIOUS));
        remoteViews.setOnClickPendingIntent(R.id.img_next, GlobalFuntion.openMusicReceiver(this, Constant.NEXT));
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause_gray);
            remoteViews.setOnClickPendingIntent(R.id.img_play, GlobalFuntion.openMusicReceiver(this, Constant.PAUSE));
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play_gray);
            remoteViews.setOnClickPendingIntent(R.id.img_play, GlobalFuntion.openMusicReceiver(this, Constant.RESUME));
        }
        remoteViews.setOnClickPendingIntent(R.id.img_close, GlobalFuntion.openMusicReceiver(this, Constant.CANNEL_NOTIFICATION));

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_small_push_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }


    public void initControl() {
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeatFlag) {
            playSong();
        } else if (shuffleFlag) {
            playShuffleSong();
        } else {
            action = Constant.NEXT;
            nextSong();
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        songLength = mediaPlayer.getDuration();
        mp.start();
        isPlaying = true;
        action = Constant.PLAY;
        sendMusicPushNotification();
        sendBroadcastChangeListener();
        changeViewCounts();
        changeSongStatus();
    }

    private void changeViewCounts() {
        int songId = listSongPlaying.get(songPosition).getId();
        MyApplication.get(this).getCountViewDatabaseReference(songId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentCount = snapshot.getValue(Integer.class);
                if (currentCount != null) {
                    int newCount = currentCount + 1;
                    MyApplication.get(MusicService.this).getCountViewDatabaseReference(songId).removeEventListener(this);
                    MyApplication.get(MusicService.this).getCountViewDatabaseReference(songId).setValue(newCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeSongStatus() {
        int songId = listSongPlaying.get(songPosition).getId();
        MyApplication.get(this).getListenedSongDatabaseReference(songId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean currentBoolean = snapshot.getValue(Boolean.class);
                if (Boolean.FALSE.equals(currentBoolean)) {
                    MyApplication.get(MusicService.this).getListenedSongDatabaseReference(songId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }


    public static void clearListSongPlaying() {
        if (listSongPlaying != null) {
            listSongPlaying.clear();
        } else {
            listSongPlaying = new ArrayList<>();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
