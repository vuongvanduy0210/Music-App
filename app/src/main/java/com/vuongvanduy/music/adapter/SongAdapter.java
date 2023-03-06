package com.vuongvanduy.music.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vuongvanduy.music.databinding.ItemSongBinding;
import com.vuongvanduy.music.model.Song;
import com.vuongvanduy.music.my_interface.IOnClickItemSongListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements Filterable {

    private List<Song> songs;
    private List<Song> listSongsOld;
    private final IOnClickItemSongListener iOnClickItemSongListener;

    public SongAdapter(IOnClickItemSongListener iOnClickItemSongListener) {
        this.iOnClickItemSongListener = iOnClickItemSongListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Song> list) {
        this.songs = list;
        this.listSongsOld = list;
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    songs = listSongsOld;
                }
                else {
                    List<Song> list = new ArrayList<>();
                    for (Song song : listSongsOld) {
                        if (song.getName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(song);
                        }
                    }

                    songs = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = songs;

                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                songs = (List<Song>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding itemSongBinding;

        public SongViewHolder(@NonNull ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.itemSongBinding = itemSongBinding;
        }
    }
}
