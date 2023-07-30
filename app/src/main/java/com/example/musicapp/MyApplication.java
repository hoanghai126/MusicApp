package com.example.musicapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.musicapp.constant.Constant;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "channel_music_id";
    private static final String CHANNEL_NAME = "channel_music_name";
    private FirebaseDatabase firebaseDatabase;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance(Constant.FIREBASE_URL);
        createChannelNotification();
    }

    private void createChannelNotification() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_MIN);
        channel.setSound(null, null);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    public DatabaseReference getSongsDatabaseReference() {
        return firebaseDatabase.getReference("/songs");
    }

    public DatabaseReference getFeedbackDatabaseReference() {
        return firebaseDatabase.getReference("/feedback");
    }

    public DatabaseReference getCountViewDatabaseReference(int songId) {
        return FirebaseDatabase.getInstance().getReference("/songs/" + songId + "/count");
    }

    public DatabaseReference getListenedSongDatabaseReference(int songId) {
        return FirebaseDatabase.getInstance().getReference("/songs/" + songId + "/listened");
    }
}
