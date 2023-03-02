package com.vuongvanduy.music.my_interface;

import com.vuongvanduy.music.model.Song;

import java.util.List;

public interface IOnClickItemSongCategoryListener {

    void onClickItemSong(Song song, List<Song> songs);
}
