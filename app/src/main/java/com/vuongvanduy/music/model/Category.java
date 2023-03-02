package com.vuongvanduy.music.model;

import java.util.List;

public class Category {

    private String name;
    private List<Song> listSongs;

    public Category(String name, List<Song> listSongs) {
        this.name = name;
        this.listSongs = listSongs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getListSongs() {
        return listSongs;
    }

    public void setListSongs(List<Song> listSongs) {
        this.listSongs = listSongs;
    }
}
