package com.vuongvanduy.music.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.adapter.SongAdapter;
import com.vuongvanduy.music.databinding.FragmentMusicDeviceBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnClickItemSongListener;
import com.vuongvanduy.music.util.MyUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeivceMusicFragment extends Fragment {

    private FragmentMusicDeviceBinding binding;

    private MainActivity mainActivity;

    private List<Song> songs;

    private SongAdapter songAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onViewCreate");
        binding = FragmentMusicDeviceBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onViewCreated");

        songs = getListSongs();

        if (songs.isEmpty()) {
            binding.tvNotiListSong.setText("No song in list. " +
                    "Please allow access photos and " +
                    "media on your device or add new song to your media storage");

            binding.tvNotiListSong.setVisibility(View.VISIBLE);
            binding.rcvListSongs.setVisibility(View.GONE);
        }

        Log.e(MyUtil.MAIN_ACTIVITY_NAME, "List song: ");
        for (Song song1 : mainActivity.songs) {
            Log.e(MyUtil.MAIN_ACTIVITY_NAME, song1.toString());
        }

        setRecylerViewSongs();

        setOnClickSearchSong();

        setOnClickButtonActionBar();
    }

    private void setRecylerViewSongs() {
        songAdapter = new SongAdapter(new IOnClickItemSongListener() {
            @Override
            public void onClickItemSong(Song song) {
                onClickPlaySong(song);
            }

            @Override
            public void onClickAddToFavourite(Song song) {

            }
        });

        songAdapter.setData(songs);
        RecyclerView rcvListSongs = binding.rcvListSongs;
        rcvListSongs.setAdapter(songAdapter);
        rcvListSongs.setLayoutManager(new LinearLayoutManager(mainActivity));
        DividerItemDecoration decoration =
                new DividerItemDecoration(mainActivity, DividerItemDecoration.VERTICAL);
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

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickSearchSong() {
        binding.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.edtSearch.setText("");
            }
        });

        binding.edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                return true;
            }
            return false;
        });
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                songAdapter.getFilter().filter(s);
            }
        });
    }

    public void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = binding.getRoot();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setOnClickButtonActionBar() {
        binding.btPlayAll.setOnClickListener(v -> {
            if (songs == null || songs.isEmpty()) {
                return;
            }

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
        List<Song> list = new ArrayList<>();

        if (mainActivity.listSongsDeivce != null) {
            list = mainActivity.listSongsDeivce;
        }

        list.sort((o1, o2) -> Collator.getInstance(
                new Locale("vi", "VN")).compare(o1.getName(), o2.getName()));

        return list;
    }

    public void onStop() {
        super.onStop();
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onDestroyView");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onResume");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(MyUtil.MUSIC_DEVICE_FRAGMENT_NAME, "onAttach");
    }
}