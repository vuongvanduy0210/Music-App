package com.vuongvanduy.music.my_interface;

import com.vuongvanduy.music.model.Song;

public interface IOnClickItemSongListener {

    void onClickItemSong(Song song);

    void onClickAddToFavourite(Song song);
}
