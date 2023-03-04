package com.vuongvanduy.music.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.adapter.SongAdapter;
import com.vuongvanduy.music.databinding.FragmentMusicDeviceBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.util.MyUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeivceMusicFragment extends Fragment {

    private FragmentMusicDeviceBinding binding;

    private MainActivity mainActivity;

    private List<Song> songs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onViewCreate");
        binding = FragmentMusicDeviceBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onViewCreated");

        songs = getListSongs();
        mainActivity.songs = songs;

        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "List song: ");
        for (Song song1 : mainActivity.songs) {
            Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, song1.toString());
        }

        setRecylerViewSongs();

        setOnClickButtonActionBar();
    }

    private void setRecylerViewSongs() {
        SongAdapter songAdapter = new SongAdapter(this::onClickPlaySong);

        songAdapter.setData(songs);
        RecyclerView rcvListSongs = binding.rcvListSongs;
        rcvListSongs.setAdapter(songAdapter);
        rcvListSongs.setLayoutManager(new LinearLayoutManager(mainActivity));
        DividerItemDecoration decoration = new DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL);
        rcvListSongs.addItemDecoration(decoration);
    }

    private void onClickPlaySong(Song song) {
        mainActivity.songs = getListSongs();
        mainActivity.listSongsDefault = getListSongs();

        mainActivity.currentSong = song;
        //start service
        mainActivity.openMusicPlayer(mainActivity);
        mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_START);
        mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_OPEN_MUSIC_PLAYER);

        mainActivity.binding.layoutMiniPlayer.setVisibility(View.VISIBLE);
        mainActivity.binding.bottomNavigation.setVisibility(View.GONE);
        mainActivity.binding.layoutMiniPlayer.setVisibility(View.GONE);
        mainActivity.binding.viewPager2.setVisibility(View.GONE);

    }

    private void setOnClickButtonActionBar() {
        binding.btPlayAll.setOnClickListener(v -> {
            mainActivity.songs = songs;
            mainActivity.currentSong = songs.get(0);

            mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_START);
            mainActivity.openMusicPlayer(mainActivity);
            mainActivity.sendActionToService(mainActivity, MyUtil.ACTION_OPEN_MUSIC_PLAYER);

            mainActivity.binding.layoutMiniPlayer.setVisibility(View.VISIBLE);
            mainActivity.binding.bottomNavigation.setVisibility(View.GONE);
            mainActivity.binding.layoutMiniPlayer.setVisibility(View.GONE);
            mainActivity.binding.viewPager2.setVisibility(View.GONE);
        });
    }

    private List<Song> getListSongs() {
        List<Song> list;

        list = mainActivity.listSongsDeivce;

        list.sort((o1, o2) -> Collator.getInstance(new Locale("vi", "VN")).compare(o1.getName(), o2.getName()));

        return list;
    }
}