package com.vuongvanduy.music.fragment;

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

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.databinding.FragmentMusicPlayerBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.service.MusicService;
import com.vuongvanduy.music.util.MyUtil;

public class MusicPlayerFragment extends Fragment {

    private FragmentMusicPlayerBinding binding;

    private MainActivity mainActivity;

    private Song currentSong;

    private boolean isPlaying, isLooping, isShuffling;

    private int finalTime;
    private int currentTime;

    private final Handler handler = new Handler();

    public class UpdateSeekBar implements Runnable {

        @Override
        public void run() {
            binding.seekBarMusic.setProgress(currentTime);

            int minutes = (currentTime/1000)/60;
            int seconds = (currentTime/1000)%60;

            @SuppressLint("DefaultLocale") String str = String.format("%02d:%02d", minutes, seconds);

            binding.tvCurrentTime.setText(str);

            handler.postDelayed(this, 1000);
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyUtil.SEND_DATA)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    currentSong = (Song) bundle.get(MyUtil.KEY_SONG);
                    int actionMusic = bundle.getInt(MyUtil.KEY_ACTION);
                    isPlaying = bundle.getBoolean(MyUtil.KEY_STATUS_MUSIC);
                    isLooping = bundle.getBoolean(MyUtil.KEY_STATUS_LOOP);
                    isShuffling = bundle.getBoolean(MyUtil.KEY_STATUS_SHUFFLE);
                    Log.e(MyUtil.MUSIC_PLAYER_FRAGMENT_NAME, "isPlaying: " + isPlaying);
                    Log.e(MyUtil.MUSIC_PLAYER_FRAGMENT_NAME, "isLooping: " + isLooping);
                    Log.e(MyUtil.MUSIC_PLAYER_FRAGMENT_NAME, "isShuffling: " + isShuffling);
                    finalTime = bundle.getInt(MyUtil.KEY_FINAL_TIME);

                    handleLayoutMusic(actionMusic);
                }
            }
        }
    };

    private final BroadcastReceiver receiverCurrentTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyUtil.SEND_CURRENT_TIME)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    currentTime = bundle.getInt(MyUtil.KEY_CURRENT_TIME);
                }
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(MyUtil.MUSIC_PLAYER_FRAGMENT_NAME, "onCreateView");
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

        assert mainActivity != null;
        LocalBroadcastManager.getInstance(mainActivity).
                registerReceiver(broadcastReceiver, new IntentFilter(MyUtil.SEND_DATA));
        LocalBroadcastManager.getInstance(mainActivity).
                registerReceiver(receiverCurrentTime, new IntentFilter(MyUtil.SEND_CURRENT_TIME));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnClickButtonListener();
    }

    private void setOnClickButtonListener() {
        binding.imgBack.setOnClickListener(v -> {
            mainActivity.binding.viewPager2.setVisibility(View.VISIBLE);
            mainActivity.binding.layoutMiniPlayer.setVisibility(View.VISIBLE);
            mainActivity.binding.bottomNavigation.setVisibility(View.VISIBLE);

            mainActivity.getSupportFragmentManager().popBackStack();
        });

        binding.imgPrevious.setOnClickListener(v ->
                mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_PREVIOUS));

        binding.imgNext.setOnClickListener(v ->
                mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_NEXT));

        binding.imgPlay.setOnClickListener(v -> {
            if (isPlaying) {
                mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_PAUSE);
            }
            else {
                mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_RESUME);
            }
        });

        binding.btShuffle.setOnClickListener(v -> {
            // neu la turn off thang shuffle thi gan lai list song default
            if (isShuffling) {
                mainActivity.resetListSongs();
            }
            else {
                mainActivity.shuffleListSongs();
//                mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_SHUFFLE);
            }
        });

        binding.btLoop.setOnClickListener(v ->
                mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_LOOP));
    }

    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic) {
            case MyUtil.ACTION_START:
//                break;
            case MyUtil.ACTION_PAUSE:
            case MyUtil.ACTION_NEXT:
            case MyUtil.ACTION_PREVIOUS:
            case MyUtil.ACTION_OPEN_MUSIC_PLAYER:
            case MyUtil.ACTION_RESUME:
            case MyUtil.ACTION_SHUFFLE:
            case MyUtil.ACTION_LOOP:
                setLayoutForMusicPlayer();
                break;
            case MyUtil.ACTION_CLEAR:
                mainActivity.getSupportFragmentManager().popBackStack();
                mainActivity.binding.viewPager2.setVisibility(View.VISIBLE);
                mainActivity.binding.bottomNavigation.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void setLayoutForMusicPlayer() {
        binding.circleImageView.setImageResource(currentSong.getImage());
        binding.tvMusicName.setText(currentSong.getName());
        binding.tvSinger.setText(currentSong.getSinger());

        int minutes = (finalTime/1000)/60;
        int seconds = (finalTime/1000)%60;
        @SuppressLint("DefaultLocale") String str = String.format("%02d:%02d", minutes, seconds);
        binding.tvFinalTime.setText(str);

        setStatusButtonPlay();
        setStatusButtonLoop();
        setStatusButtonShuffle();
        setAnimationForDisk();
        setStatusSeekBarMusic();
    }

    private void setStatusSeekBarMusic() {
        binding.seekBarMusic.setMax(finalTime);

        binding.seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // gui current time lai cho service
                    sendActionToService(mainActivity, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        UpdateSeekBar updateSeekBar = new UpdateSeekBar();

        handler.post(updateSeekBar);
    }

    private void sendActionToService(Context context, int progress) {
        Intent intentActivity = new Intent(context, MusicService.class);

        intentActivity.putExtra(MyUtil.KEY_ACTION, MyUtil.ACTION_CONTROL_SEEK_BAR);
        intentActivity.putExtra(MyUtil.KEY_PROGRESS, progress);

        context.startService(intentActivity);
    }

    private void setStatusButtonPlay() {
        if (isPlaying) {
            binding.imgPlay.setImageResource(R.drawable.ic_pause);
        }
        else {
            binding.imgPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void setStatusButtonLoop() {
        if (isLooping) {
            binding.btLoop.setImageResource(R.drawable.ic_is_looping);
        }
        else {
            binding.btLoop.setImageResource(R.drawable.ic_loop);
        }
    }

    private void setStatusButtonShuffle() {
        if (isShuffling) {
            binding.btShuffle.setImageResource(R.drawable.ic_is_shuffling);
        }
        else {
            binding.btShuffle.setImageResource(R.drawable.ic_shuffle);
        }
    }

    public void setAnimationForDisk() {
        if (isPlaying) {
            startAnimation();
        }
        else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                binding.circleImageView.animate()
                        .rotationBy(360).withEndAction(this).setDuration(10000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        binding.circleImageView.animate()
                .rotationBy(360).withEndAction(runnable).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnimation() {
        binding.circleImageView.animate().cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MusicPlayerFragment", "onDestroy");
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(mainActivity).unregisterReceiver(receiverCurrentTime);
    }
}