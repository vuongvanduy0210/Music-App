package com.vuongvanduy.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.vuongvanduy.music.R;
import com.vuongvanduy.music.databinding.ItemCategoryBinding;
import com.vuongvanduy.music.fragment.MusicPlayerFragment;
import com.vuongvanduy.music.model.Category;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnclickItemCategoryListener;
import com.vuongvanduy.music.service.MusicService;
import com.vuongvanduy.music.util.MyUtil;

import java.io.Serializable;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> listCategories;

    private final IOnclickItemCategoryListener iOnclickItemCategoryListener;

    private final Context context;

    public CategoryAdapter(Context context, List<Category> listCategories, IOnclickItemCategoryListener listner) {
        this.listCategories = listCategories;
        this.context = context;
        this.iOnclickItemCategoryListener = listner;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCategories.get(position);

        if (category == null) {
            return;
        }

        holder.binding.tvNameCategory.setText(category.getName());

        SongCategoryAdapter adapter = new SongCategoryAdapter(category.getListSongs(), this::startMusic);
        List<Song> listSongsPlay = iOnclickItemCategoryListener.getListSongPlay(category.getName());
        adapter.setData(listSongsPlay);
        GridLayoutManager manager = new GridLayoutManager(context, 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.binding.rcvSong.setLayoutManager(manager);
        holder.binding.rcvSong.setAdapter(adapter);

        holder.binding.btViewAlls.setOnClickListener(v ->
                iOnclickItemCategoryListener.clickButtonViewAlls(category.getName()));

        holder.binding.tvNameCategory.setOnClickListener(v ->
                iOnclickItemCategoryListener.clickButtonViewAlls(category.getName()));
    }

    private void startMusic(Song song, List<Song> songs) {
        openMusicPlayer((FragmentActivity) context);
        sendActionToService(context, MyUtil.ACTION_START, song, songs);
        sendActionToService(context, MyUtil.ACTION_OPEN_MUSIC_PLAYER, song, songs);

        iOnclickItemCategoryListener.openMusicPlayer();
    }

    public void openMusicPlayer(FragmentActivity fragmentActivity) {
        MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();

        FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_right)
                .replace(R.id.layout_fragment, musicPlayerFragment)
                .addToBackStack("Backstack 1")
                .commit();
    }

    public void sendActionToService(Context context, int action, Song currentSong, List<Song> songs) {
        Log.e(MyUtil.MAIN_ACTIVITY_NAME, String.valueOf(action));
        Intent intentActivity = new Intent(context, MusicService.class);

        intentActivity.putExtra(MyUtil.KEY_ACTION, action);

        Bundle bundle = new Bundle();
        bundle.putSerializable(MyUtil.KEY_LIST_SONGS, (Serializable) songs);
        bundle.putSerializable(MyUtil.KEY_SONG, currentSong);
        intentActivity.putExtras(bundle);

        context.startService(intentActivity);
    }

    @Override
    public int getItemCount() {
        if (listCategories != null) {
            return listCategories.size();
        }
        return 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
