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

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.adapter.SongAdapter;

import com.vuongvanduy.music.databinding.FragmentAllMusicBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnClickItemSongListener;
import com.vuongvanduy.music.util.MyUtil;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AllMusicFragment extends Fragment {

    private FragmentAllMusicBinding binding;

    private MainActivity mainActivity;

    private List<Song> songs;

    private SongAdapter songAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onCreateView");
        binding = FragmentAllMusicBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onViewCreated");

        songs = getListSongs();

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
            mainActivity.listSongsDefault = songs;
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

    @Override
    public void onStop() {
        super.onStop();
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onDestroyView");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onResume");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e(MyUtil.ALL_MUSIC_FRAGMENT_NAME, "onAttach");
    }

    private List<Song> getListSongs() {
        List<Song> list = new ArrayList<>();

        list.add(new Song("Waiting for you", "Mono", R.drawable.img_waiting_for_you, R.raw.waiting_for_you));
        list.add(new Song("Y??u em r???t nhi???u", "Ho??ng T??n", R.drawable.img_yeu_em_rat_nhieu, R.raw.yeu_em_rat_nhieu));
        list.add(new Song("Nh?? nh???ng ph??t ban ?????u", "Ho??i L??m", R.drawable.img_nhu_nhung_phut_ban_dau, R.raw.nhu_nhung_phut_ban_dau));
        list.add(new Song("B???c ph???n", "Jack", R.drawable.img_bac_phan, R.raw.bac_phan));
        list.add(new Song("?????ng nh?? th??i quen", "Jaykii - Sara L??u", R.drawable.img_dung_nhu_thoi_quen, R.raw.dung_nhu_thoi_quen));
        list.add(new Song("N??m ???y", "?????c Ph??c", R.drawable.img_nam_ay, R.raw.nam_ay));
        list.add(new Song("N??ng th??", "Ho??ng D??ng", R.drawable.img_nang_tho, R.raw.nang_tho));
        list.add(new Song("Ng?????i ???y", "Tr???nh Th??ng B??nh", R.drawable.img_nguoi_ay, R.raw.nguoi_ay));
        list.add(new Song("Ph??a sau m???t c?? g??i", "Soobin Ho??ng S??n", R.drawable.img_phia_sau_1_co_gai, R.raw.phia_sau_mot_co_gai));
        list.add(new Song("Ai chung t??nh ???????c m??i", "Ho??i L??m Cover", R.drawable.img_ai_chung_tinh_duoc_mai, R.raw.ai_chung_tinh_duoc_mai));
        list.add(new Song("Ch???m kh??? tim anh m???t ch??t th??i", "Noo Ph?????c Th???nh", R.drawable.img_cham_khe_tim_anh_mot_chut_thoi, R.raw.cham_khe_tim_anh_mot_chut_thoi));
        list.add(new Song("Chi???u h??m ???y", "Jaykii", R.drawable.img_chieu_hom_ay, R.raw.chieu_hom_ay));
        list.add(new Song("?????ng qu??n t??n anh", "ALEX Lam Cover", R.drawable.img_dung_quen_ten_anh, R.raw.dung_quen_ten_anh));
        list.add(new Song("H??m nay em c?????i r???i", "Kh???i ????ng", R.drawable.img_hom_nay_em_cuoi_roi, R.raw.hom_nay_em_cuoi_roi));
        list.add(new Song("N??i t??nh y??u b???t ?????u", "B??i Anh Tu???n", R.drawable.img_noi_tinh_yeu_bat_dau, R.raw.noi_ty_bat_dau));
        list.add(new Song("Su??t n???a th??", "Andiez", R.drawable.img_suyt_nua_thi, R.raw.suyt_nua_thi));
        list.add(new Song("Th????ng em l?? ??i???u anh kh??ng th??? ng???", "Noo Ph?????c Th???nh", R.drawable.img_thuong_em_la_dieu_anh_khong_the_ngo, R.raw.thuong_em_la_dieu_anh_khong_the_ngo));
        list.add(new Song("Th????ng em", "Ch??u Kh???i Phong", R.drawable.img_thuong_em, R.raw.thuong_em));

        list.sort((o1, o2) -> Collator.getInstance(new Locale("vi", "VN")).compare(o1.getName(), o2.getName()));

        return list;
    }
}