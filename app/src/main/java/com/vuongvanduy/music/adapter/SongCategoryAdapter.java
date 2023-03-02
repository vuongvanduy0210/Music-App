package com.vuongvanduy.music.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vuongvanduy.music.databinding.ItemSongInCategoryBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnClickItemSongCategoryListener;

import java.util.List;

public class SongCategoryAdapter extends RecyclerView.Adapter<SongCategoryAdapter.SongCategoryViewHolder> {

    private final List<Song> listSongsShow;
    private List<Song> listSongsPlay;

    private final IOnClickItemSongCategoryListener listener;

    public SongCategoryAdapter(List<Song> list, IOnClickItemSongCategoryListener listener) {
        this.listSongsShow = list;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Song> list) {
        this.listSongsPlay = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongInCategoryBinding binding = ItemSongInCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongCategoryViewHolder holder, int position) {
        Song song = listSongsShow.get(position);

        if (song == null) {
            return;
        }

        holder.binding.imgSong.setImageResource(song.getImage());
        holder.binding.tvNameSong.setText(song.getName());
        holder.binding.tvSinger.setText(song.getSinger());

        holder.binding.layoutItemSong.setOnClickListener(v -> {
            listener.onClickItemSong(song, listSongsPlay);
        });
    }

    @Override
    public int getItemCount() {
        if (listSongsShow != null) {
            return listSongsShow.size();
        }
        return 0;
    }

    public static class SongCategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongInCategoryBinding binding;

        public SongCategoryViewHolder(@NonNull ItemSongInCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
