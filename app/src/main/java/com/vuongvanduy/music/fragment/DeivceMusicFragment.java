package com.vuongvanduy.music.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.databinding.FragmentMusicDeviceBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.util.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class MusicDeviceFragment extends Fragment {

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
        songs = mainActivity.listSongsDeivce;
    }

}