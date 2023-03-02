package com.vuongvanduy.music.my_interface;

import com.vuongvanduy.music.model.Song;

import java.util.List;

public interface IOnclickItemCategoryListener {

    void clickButtonViewAlls(String categoryName);

    void openMusicPlayer();

    List<Song> getListSongPlay(String categoryName);
}
