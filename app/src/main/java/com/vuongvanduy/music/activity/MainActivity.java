package com.vuongvanduy.music.activity;

import static com.vuongvanduy.music.util.MyUtil.REQUEST_PERMISSION_READ_EXTERNAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import com.vuongvanduy.music.R;

import com.vuongvanduy.music.adapter.FragmentViewPager2Adapter;

import com.vuongvanduy.music.databinding.ActivityMainBinding;
import com.vuongvanduy.music.view_pager_transformer.ZoomOutPageTransformer;

import com.vuongvanduy.music.fragment.MusicPlayerFragment;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.service.MusicService;
import com.vuongvanduy.music.util.MyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    public boolean isPlaying;
    public Song currentSong;
    public List<Song> songs;
    public List<Song> listSongsDefault;
    public List<Song> listSongsDeivce;

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

            Log.e(MyUtil.MAIN_ACTIVITY_NAME, "List song receive: ");
            for (Song song1 : songs) {
                Log.e(MyUtil.MAIN_ACTIVITY_NAME, song1.toString());
            }

            handleLayoutMusic(actionMusic);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(MyUtil.MAIN_ACTIVITY_NAME, "onCreate");
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        songs = new ArrayList<>();

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(MyUtil.SEND_DATA));

        requestPermission();

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
        AHBottomNavigationItem itemDevice = new AHBottomNavigationItem(
                R.string.menu_deivce_title, R.drawable.ic_device, R.color.orange);

        binding.bottomNavigation.addItem(itemHome);
        binding.bottomNavigation.addItem(itemAll);
        binding.bottomNavigation.addItem(itemFavourite);
        binding.bottomNavigation.addItem(itemDevice);

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

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // get list song from device and send to music device fragment
            listSongsDeivce = getListSongFromDevice();
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // get list song from device and send to music device fragment
            listSongsDeivce = getListSongFromDevice();
        }
        else {
            String[] permission ={Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, REQUEST_PERMISSION_READ_EXTERNAL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // check request code = voi REQUEST_FINE_LOCATION
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL) {
            // neu nhu dong y
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Get music from your phone success", Toast.LENGTH_SHORT).show();
                // get list song from device and send to music device fragment
                listSongsDeivce = getListSongFromDevice();
            } else {
                // neu nhu tu choi
                Toast.makeText(this, "Can't get music from your phone", Toast.LENGTH_SHORT).show();
                Log.e(MyUtil.MAIN_ACTIVITY_NAME, "Permission denied");
            }
        }
    }

    private List<Song> getListSongFromDevice() {
        List<Song> songs = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor == null) {
            Toast.makeText(this, "Something Went Wrong.", Toast.LENGTH_SHORT);
        } else if (!cursor.moveToFirst()) {
            Toast.makeText(this, "No Music Found on SD Card.", Toast.LENGTH_SHORT);
        } else {
            //get columns
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(titleColumn);
                String singer = cursor.getString(artistColumn);

                Song song = new Song(name, singer, R.drawable.icon_app, 0);
                song.setId(id);
                songs.add(song);

            } while (cursor.moveToNext());
        }
        return songs;
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