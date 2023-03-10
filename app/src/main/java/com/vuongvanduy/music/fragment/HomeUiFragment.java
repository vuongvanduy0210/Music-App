package com.vuongvanduy.music.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vuongvanduy.music.R;
import com.vuongvanduy.music.activity.MainActivity;
import com.vuongvanduy.music.adapter.CategoryAdapter;
import com.vuongvanduy.music.adapter.PhotoViewPager2Adapter;
import com.vuongvanduy.music.databinding.FragmentHomeUiBinding;
import com.vuongvanduy.music.model.Category;
import com.vuongvanduy.music.model.Photo;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnclickItemCategoryListener;
import com.vuongvanduy.music.util.MyUtil;
import com.vuongvanduy.music.view_pager_transformer.DepthPageTransformer;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeUiFragment extends Fragment {

    private FragmentHomeUiBinding binding;

    private MainActivity mainActivity;

    private List<Photo> photos;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.slideImage.getCurrentItem() ==  photos.size() - 1) {
                binding.slideImage.setCurrentItem(0);
            }
            else {
                binding.slideImage.setCurrentItem(binding.slideImage.getCurrentItem() + 1);
            }
        }
    };

    public HomeUiFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(MyUtil.HOME_UI_FRAGMENT_NAME, "onCreateView");
        binding = FragmentHomeUiBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(MyUtil.HOME_UI_FRAGMENT_NAME, "onViewCreated");

        setAutoSlideImage();

        setRecyclerViewCategory();
    }
    
    public void setRecyclerViewCategory() {
        CategoryAdapter adapter = new CategoryAdapter(mainActivity, getListCategory(), new IOnclickItemCategoryListener() {
            @Override
            public void clickButtonViewAlls(String categoryName) {
                goToViewAlls(categoryName);
            }

            @Override
            public void openMusicPlayer() {
                mainActivity.binding.layoutMiniPlayer.setVisibility(View.VISIBLE);
                mainActivity.binding.bottomNavigation.setVisibility(View.GONE);
                mainActivity.binding.layoutMiniPlayer.setVisibility(View.GONE);
                mainActivity.binding.viewPager2.setVisibility(View.GONE);
            }

            @Override
            public List<Song> getListSongPlay(String categoryName) {
                return getListSongsPlay(categoryName);
            }
        });

        binding.rcvCategory.setLayoutManager(new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false));
        binding.rcvCategory.setAdapter(adapter);
    }

    private void goToViewAlls(String categoryName) {
        if (categoryName.equals("All Songs")) {
            mainActivity.binding.viewPager2.setCurrentItem(1);
        }
        else if (categoryName.equals("Favourite Songs")) {
            mainActivity.binding.viewPager2.setCurrentItem(2);
        }
    }

    private List<Category> getListCategory() {
        List<Category> categories = new ArrayList<>();

        List<Song> songs1 = getFavouriteSongsShow();
        List<Song> songs2 = getAllSongsShow();

        categories.add(new Category("Favourite Songs", songs1));
        categories.add(new Category("All Songs", songs2));

        return categories;
    }

    public List<Song> getListSongsPlay(String categoryName) {
        if (categoryName.equals("All Songs")) {
            mainActivity.listSongsDefault = getAllSongs();
            return getAllSongs();
        }
        else if (categoryName.equals("Favourite Songs")) {
            mainActivity.listSongsDefault = getFavouriteSongs();
            return getFavouriteSongs();
        }
        return null;
    }

    private void setAutoSlideImage() {
        photos = getListPhotos();
        PhotoViewPager2Adapter adapter = new PhotoViewPager2Adapter(photos);
        binding.slideImage.setAdapter(adapter);

        binding.slideImage.setPageTransformer(new DepthPageTransformer());

        binding.circleIndicator.setViewPager(binding.slideImage);

        binding.slideImage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 2500);
            }
        });
    }

    private List<Photo> getListPhotos() {
        List<Photo> list = new ArrayList<>();

        list.add(new Photo(R.drawable.img_bac_phan));
        list.add(new Photo(R.drawable.img_dung_nhu_thoi_quen));
        list.add(new Photo(R.drawable.img_yeu_em_rat_nhieu));
        list.add(new Photo(R.drawable.img_nang_tho));
        list.add(new Photo(R.drawable.img_nguoi_ay));

        return list;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(MyUtil.HOME_UI_FRAGMENT_NAME, "onPause");
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 2000);
    }

    private List<Song> getAllSongsShow() {
        List<Song> list = new ArrayList<>();

        list.add(new Song("N??ng th??", "Ho??ng D??ng", R.drawable.img_nang_tho, R.raw.nang_tho));
        list.add(new Song("Ng?????i ???y", "Tr???nh Th??ng B??nh", R.drawable.img_nguoi_ay, R.raw.nguoi_ay));
        list.add(new Song("Ph??a sau m???t c?? g??i", "Soobin Ho??ng S??n", R.drawable.img_phia_sau_1_co_gai, R.raw.phia_sau_mot_co_gai));
        list.add(new Song("Th????ng em", "Ch??u Kh???i Phong", R.drawable.img_thuong_em, R.raw.thuong_em));

        list.sort((o1, o2) -> Collator.getInstance(new Locale("vi", "VN")).compare(o1.getName(), o2.getName()));

        return list;
    }

    private List<Song> getFavouriteSongsShow() {
        List<Song> list = new ArrayList<>();

        list.add(new Song("Y??u em r???t nhi???u", "Ho??ng T??n", R.drawable.img_yeu_em_rat_nhieu, R.raw.yeu_em_rat_nhieu));
        list.add(new Song("Chi???u h??m ???y", "Jaykii", R.drawable.img_chieu_hom_ay, R.raw.chieu_hom_ay));
        list.add(new Song("Ph??a sau m???t c?? g??i", "Soobin Ho??ng S??n", R.drawable.img_phia_sau_1_co_gai, R.raw.phia_sau_mot_co_gai));
        list.add(new Song("?????ng nh?? th??i quen", "Jaykii - Sara L??u", R.drawable.img_dung_nhu_thoi_quen, R.raw.dung_nhu_thoi_quen));

        list.sort((o1, o2) -> Collator.getInstance(new Locale("vi", "VN")).compare(o1.getName(), o2.getName()));

        return list;
    }

    private List<Song> getAllSongs() {
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

    private List<Song> getFavouriteSongs() {
        List<Song> list = new ArrayList<>();

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