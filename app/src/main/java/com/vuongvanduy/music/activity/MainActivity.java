package com.vuongvanduy.music.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.databinding.ActivityMainBinding;
import com.vuongvanduy.music.adapter.FragmentViewPager2Adapter;

import com.vuongvanduy.music.view_pager_transformer.ZoomOutPageTransformer;

import com.vuongvanduy.music.fragment.MusicPlayerFragment;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.service.MusicService;
import com.vuongvanduy.music.util.MyUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    public boolean isPlaying;
    public Song currentSong;
    public List<Song> songs;
    public List<Song> listSongsDefault;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            currentSong = (Song) bundle.get(MyUtil.KEY_SONG);
            isPlaying = bundle.getBoolean(MyUtil.KEY_STATUS_MUSIC);
            Log.e(MyUtil.MAIN_ACTIVITY_NAME, "isPlaying: " + isPlaying);
            int actionMusic = bundle.getInt(MyUtil.KEY_ACTION);
            songs = (List<Song>) bundle.getSerializable(MyUtil.KEY_LIST_SONGS);

            handleLayoutMusic(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(MyUtil.MAIN_ACTIVITY_NAME, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(MyUtil.SEND_DATA));

        setViewPager2Adapter();

        setOnClickLisnterButtonOnMiniPlayer();

        setListenerViewPagerAndBottomNavigation();
    }

    private void setViewPager2Adapter() {
        FragmentViewPager2Adapter adapter = new FragmentViewPager2Adapter(this);
        binding.viewPager2.setAdapter(adapter);
        binding.viewPager2.setUserInputEnabled(false);
        binding.viewPager2.setPageTransformer(new ZoomOutPageTransformer());
    }

    private void setOnClickLisnterButtonOnMiniPlayer() {
        // set on click button on mini player
        binding.imgPlay.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(this, MyUtil.ACTION_PAUSE);
            }
            else {
                sendActionToService(this, MyUtil.ACTION_RESUME);
            }
        });

        binding.imgNext.setOnClickListener(v ->
                sendActionToService(this, MyUtil.ACTION_NEXT));

        binding.imgPrevious.setOnClickListener(v ->
                sendActionToService(this, MyUtil.ACTION_PREVIOUS));

        binding.layoutMiniPlayer.setOnClickListener(v -> onClickMiniPlayer());
    }

    public void sendActionToService(Context context, int action) {
        Log.e(MyUtil.MAIN_ACTIVITY_NAME, String.valueOf(action));
        Intent intentActivity = new Intent(context, MusicService.class);

        intentActivity.putExtra(MyUtil.KEY_ACTION, action);

        Bundle bundle = new Bundle();
        bundle.putSerializable(MyUtil.KEY_LIST_SONGS, (Serializable) songs);
        bundle.putSerializable(MyUtil.KEY_SONG, currentSong);
//        bundle.putBoolean(MyUtil.KEY_STATUS_SHUFFLE, isShuffling);
        intentActivity.putExtras(bundle);

        context.startService(intentActivity);
    }



    public void onClickMiniPlayer() {
        binding.bottomNavigation.setVisibility(View.GONE);
        binding.layoutMiniPlayer.setVisibility(View.GONE);
        binding.viewPager2.setVisibility(View.GONE);

        //send data and open music player
        openMusicPlayer(this);
        if (!isPlaying) {
            sendActionToService(this, MyUtil.ACTION_RESUME);
        }
        sendActionToService(this, MyUtil.ACTION_OPEN_MUSIC_PLAYER);

    }

    public void openMusicPlayer(FragmentActivity fragmentActivity) {
        MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();

//        fragmentActivity.getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction =
                fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_fragment, musicPlayerFragment);
        transaction.addToBackStack("Backstack 1");
        transaction.commit();
    }

    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic) {
            case MyUtil.ACTION_START:
            case MyUtil.ACTION_NEXT:
            case MyUtil.ACTION_PREVIOUS:
                setLayoutForMiniPlayer(currentSong);
                setStatusButtonPlay();
                Log.e(MyUtil.MAIN_ACTIVITY_NAME, "IsPlaying: " + isPlaying);
                break;
            case MyUtil.ACTION_PAUSE:
            case MyUtil.ACTION_RESUME:
                setStatusButtonPlay();
                Log.e(MyUtil.MAIN_ACTIVITY_NAME, "IsPlaying: " + isPlaying);
                break;
            case MyUtil.ACTION_CLEAR:
                isPlaying = false;
                setStatusButtonPlay();
                binding.layoutMiniPlayer.setVisibility(View.GONE);
                break;
        }
    }

    private void setLayoutForMiniPlayer(Song song) {
        // set layout
        binding.imgMusic.setImageResource(song.getImage());
        binding.tvMusicName.setText(song.getName());
        binding.tvSinger.setText(song.getSinger());
    }

    private void setStatusButtonPlay() {
        if (isPlaying) {
            binding.imgPlay.setImageResource(R.drawable.ic_pause);
        }
        else {
            binding.imgPlay.setImageResource(R.drawable.ic_play);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void setListenerViewPagerAndBottomNavigation() {

        AHBottomNavigationItem itemAll = new AHBottomNavigationItem(
                R.string.menu_all_title, R.drawable.ic_all, R.color.teal_200);
        AHBottomNavigationItem itemHome = new AHBottomNavigationItem(
                R.string.menu_home_title, R.drawable.ic_home, R.color.blueLight);
        AHBottomNavigationItem itemFavourite = new AHBottomNavigationItem(
                R.string.menu_favourite_title, R.drawable.ic_favourite, R.color.red);

        binding.bottomNavigation.addItem(itemHome);
        binding.bottomNavigation.addItem(itemAll);
        binding.bottomNavigation.addItem(itemFavourite);

        binding.bottomNavigation.setColored(true);

        binding.bottomNavigation.setAccentColor(Color.parseColor("#FFFFFFFF"));
        binding.bottomNavigation.setInactiveColor(Color.parseColor("#120433"));

        binding.bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {

            binding.viewPager2.setCurrentItem(position);

            return true;
        });

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bottomNavigation.setCurrentItem(position);
            }
        });
    }

    public void shuffleListSongs() {
        Collections.shuffle(songs);
        Log.e(MyUtil.MAIN_ACTIVITY_NAME, "List song shuffle on: ");
        for (Song song1 : songs) {
            Log.e(MyUtil.MAIN_ACTIVITY_NAME, song1.toString());
        }
        sendActionToService(this, MyUtil.ACTION_SHUFFLE);
    }

    public void resetListSongs() {
        songs = listSongsDefault;
        if (songs != null) {
            Log.e(MyUtil.MAIN_ACTIVITY_NAME, "List song shuffle off: ");
            for (Song song1 : songs) {
                Log.e(MyUtil.MAIN_ACTIVITY_NAME, song1.toString());
            }
            sendActionToService(this, MyUtil.ACTION_SHUFFLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //
        binding.bottomNavigation.setVisibility(View.VISIBLE);
        binding.layoutMiniPlayer.setVisibility(View.VISIBLE);
        binding.viewPager2.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainActivity", "onStop");
    }

    @Override
    protected void onPause() {
        Log.e("MainActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity", "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume");
    }
}