package com.vuongvanduy.music.broadcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vuongvanduy.music.service.MusicService;
import com.vuongvanduy.music.util.MyUtil;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int actionMusic = intent.getIntExtra(MyUtil.ACTION_MUSIC_NAME, 0);

        Intent intentService = new Intent(context, MusicService.class);
        intentService.putExtra(MyUtil.KEY_ACTION, actionMusic);

        context.startService(intentService);
    }
}
