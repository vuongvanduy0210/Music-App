package com.vuongvanduy.music.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.broadcast_receiver.MyReceiver;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.util.MyUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static boolean isPlaying, isLooping, isShuffling;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private List<Song> songs;

    private int currentTime;
    private int finalTime;

    private int progressReceive;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                currentTime = mediaPlayer.getCurrentPosition();
                updateCurrentTime();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(MyUtil.MUSIC_SERVICE_NAME, "isShuffling: " + isShuffling);
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "isLooping: " + isLooping);

        //receive from activity
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            //recieve list songs
            List<Song> listReceive = (List<Song>) bundle.get(MyUtil.KEY_LIST_SONGS);
            if (listReceive != null) {
                songs = listReceive;
                Log.e(MyUtil.MUSIC_SERVICE_NAME, "List song receive: ");
                for (Song song1 : songs) {
                    Log.e(MyUtil.MUSIC_SERVICE_NAME, song1.toString());
                }
            }
            else {
                Log.e(MyUtil.MUSIC_SERVICE_NAME, "Get list song fail");
            }

            //receive song
            Song songReceive = (Song) bundle.getSerializable(MyUtil.KEY_SONG);
            if (songReceive != null) {
                currentSong = songReceive;
                Log.e(MyUtil.MUSIC_SERVICE_NAME, "Song receive: " + songReceive);
            }
            else {
                Log.e(MyUtil.MUSIC_SERVICE_NAME, "Receive song fail");
            }
        }

        //receive progress
        progressReceive = intent.getIntExtra(MyUtil.KEY_PROGRESS, 0);
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "Action receive: " + progressReceive);
        if (progressReceive != 0) {
            // thuc hien hanh dong sau khi nhan action tu activity hoac fragment
            handleActionMusic(MyUtil.ACTION_CONTROL_SEEK_BAR);
        }

        //receive action
        int actionMusicReceive = intent.getIntExtra(MyUtil.KEY_ACTION, 0);
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "Action receive: " + actionMusicReceive);
        if (actionMusicReceive != 0) {
            // thuc hien hanh dong sau khi nhan action tu activity hoac fragment
            handleActionMusic(actionMusicReceive);
        }



        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case MyUtil.ACTION_START:
                startPlayerMusic();
                break;
            case MyUtil.ACTION_PREVIOUS:
                previousMusic();
                break;
            case MyUtil.ACTION_PAUSE:
                pauseMusic();
                break;
            case MyUtil.ACTION_RESUME:
                resumeMusic();
                break;
            case MyUtil.ACTION_NEXT:
                nextMusic();
                break;
            case MyUtil.ACTION_CLEAR:
                clearMusic();
                break;
            case MyUtil.ACTION_OPEN_MUSIC_PLAYER:
                sendDataToMiniPlayer();
                break;
            case MyUtil.ACTION_CONTROL_SEEK_BAR:
                mediaPlayer.seekTo(progressReceive);
                break;
            case MyUtil.ACTION_SHUFFLE:
                shuffleMusic();
                break;
            case MyUtil.ACTION_LOOP:
                loopMusic();
                break;
        }
    }

    private void startPlayerMusic() {
        startMusic(currentSong);
        sendNotification(currentSong);
        sendData(MyUtil.ACTION_START, songs);
    }

    private void previousMusic() {
        int index = getIndexFromListSong(currentSong);

        Log.e(MyUtil.MUSIC_SERVICE_NAME, "Index of Song: " + index);

        if (index == -1) {
            return;
        }
        if (index == 0) {
            index = songs.size();
        }
        currentSong = songs.get(index - 1);
        startMusic(currentSong);
        sendNotification(currentSong);
        sendData(MyUtil.ACTION_PREVIOUS, songs);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(currentSong);
            sendData(MyUtil.ACTION_PAUSE, songs);
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            updateCurrentTime();
            isPlaying = true;
            sendNotification(currentSong);
            sendData(MyUtil.ACTION_RESUME, songs);
        }
    }

    private void nextMusic() {
        int index = getIndexFromListSong(currentSong);

        Log.e(MyUtil.MUSIC_SERVICE_NAME, "Index of Song: " + index);

        if (index == -1) {
            return;
        }
        if (index == (songs.size() - 1)) {
            index = -1;
        }
        currentSong = songs.get(index + 1);
        startMusic(currentSong);
        sendNotification(currentSong);
        sendData(MyUtil.ACTION_NEXT, songs);
    }

    private void clearMusic() {
        stopSelf();
        isPlaying = false;
        sendData(MyUtil.ACTION_CLEAR, songs);
    }

    private void sendDataToMiniPlayer() {
        sendData(MyUtil.ACTION_OPEN_MUSIC_PLAYER, songs);
    }

    private void shuffleMusic() {
        isShuffling = !isShuffling;
        sendData(MyUtil.ACTION_SHUFFLE, songs);
    }

    private void loopMusic() {
        isLooping = !isLooping;
        sendData(MyUtil.ACTION_LOOP, songs);
    }

    private void startMusic(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (song.getId() == 0) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.start();
        }
        else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            long id = song.getId();
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

            try {
                mediaPlayer.setDataSource(getApplicationContext(), trackUri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e(MyUtil.MUSIC_SERVICE_NAME, "Error setting data source");
            }
            finalTime = mediaPlayer.getDuration();
            isPlaying = true;

            Log.e(MyUtil.MUSIC_SERVICE_NAME, "Start music success");
            return;
        }

        finalTime = mediaPlayer.getDuration();
        isPlaying = true;

        updateCurrentTime();
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "Start music success");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "onPrepared");
        mp.start();
        updateCurrentTime();
    }

    private int getIndexFromListSong(Song s) {
        int index = -1;
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getName().equalsIgnoreCase(s.getName()) &&
                    songs.get(i).getSinger().equalsIgnoreCase(s.getSinger())) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void sendNotification(Song song) {
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "onSendNotification");
        //set layout notification
        Bitmap bitmapImageSong = BitmapFactory.decodeResource(getResources(), song.getImage());

        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification_music);
        notificationLayout.setTextViewText(R.id.tv_music_name_in_notification, song.getName());
        notificationLayout.setTextViewText(R.id.tv_singer_in_notification, song.getSinger());
        notificationLayout.setImageViewBitmap(R.id.img_music_in_notification, bitmapImageSong);

        //set on click notfication
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE);

        // set on click button in notification
        if (isPlaying) {
            notificationLayout.setOnClickPendingIntent(R.id.img_play_in_notification,
                    getPendingIntent(this, MyUtil.ACTION_PAUSE));
            notificationLayout.setImageViewResource(R.id.img_play_in_notification, R.drawable.ic_pause);
        }
        else {
            notificationLayout.setOnClickPendingIntent(R.id.img_play_in_notification,
                    getPendingIntent(this, MyUtil.ACTION_RESUME));
            notificationLayout.setImageViewResource(R.id.img_play_in_notification, R.drawable.ic_play);
        }

        notificationLayout.setOnClickPendingIntent(R.id.img_clear_in_notification,
                getPendingIntent(this, MyUtil.ACTION_CLEAR));
        notificationLayout.setOnClickPendingIntent(R.id.img_previous_in_notification,
                getPendingIntent(this, MyUtil.ACTION_PREVIOUS));
        notificationLayout.setOnClickPendingIntent(R.id.img_next_in_notification,
                getPendingIntent(this, MyUtil.ACTION_NEXT));

        // send notification
        Notification notification = new NotificationCompat.Builder(this, MyUtil.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setSound(null)
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(1, notification);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra(MyUtil.ACTION_MUSIC_NAME, action);

        return PendingIntent.getBroadcast(context.getApplicationContext(),
                action, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void updateCurrentTime() {
        sendCurrentTime();
        handler.postDelayed(runnable, 10);
    }

    private void sendData(int action, List<Song> list) {
        Intent intent = new Intent(MyUtil.SEND_DATA);
        Bundle bundle = new Bundle();

        bundle.putInt(MyUtil.KEY_ACTION, action);
        bundle.putSerializable(MyUtil.KEY_SONG, currentSong);
        bundle.putInt(MyUtil.KEY_FINAL_TIME, finalTime);
        bundle.putBoolean(MyUtil.KEY_STATUS_MUSIC, isPlaying);
        bundle.putBoolean(MyUtil.KEY_STATUS_SHUFFLE, isShuffling);
        bundle.putBoolean(MyUtil.KEY_STATUS_LOOP, isLooping);
        bundle.putSerializable(MyUtil.KEY_LIST_SONGS, (Serializable) list);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.e(MyUtil.MUSIC_SERVICE_NAME, "Send action to activity success");
    }

    private void sendCurrentTime() {
        Intent intent = new Intent(MyUtil.SEND_CURRENT_TIME);
        Bundle bundle = new Bundle();
        bundle.putSerializable(MyUtil.KEY_CURRENT_TIME, currentTime);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//        Log.e(MyUtil.MUSIC_SERVICE_NAME, "send current time success");
    }

    // auto next music
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer == null) {
            return;
        }

        if (isLooping) {
            mp.start();
        }

        else {
            new Handler().postDelayed(this::nextMusic, 1700);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MusicService", "onCreate");
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
