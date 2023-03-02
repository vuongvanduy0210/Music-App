package com.vuongvanduy.music.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vuongvanduy.music.databinding.ItemSongBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnClickItemSongListener;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songs;
    private final IOnClickItemSongListener iOnClickItemSongListener;

    public SongAdapter(IOnClickItemSongListener iOnClickItemSongListener) {
        this.iOnClickItemSongListener = iOnClickItemSongListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Song> list) {
        this.songs = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(itemSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        if (song == null) {
            return;
        }

        holder.itemSongBinding.tvMusicNameInList.setText(song.getName());
        holder.itemSongBinding.tvSingerInList.setText(song.getSinger());
        holder.itemSongBinding.imgMusicInList.setImageResource(song.getImage());
        holder.itemSongBinding.layoutItem.setOnClickListener(v ->
                iOnClickItemSongListener.onClickItemSong(song));
    }

    @Override
    public int getItemCount() {
        if (songs != null) {
            return songs.size();
        }
        return 0;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding itemSongBinding;

        public SongViewHolder(@NonNull ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.itemSongBinding = itemSongBinding;
        }
    }
}
