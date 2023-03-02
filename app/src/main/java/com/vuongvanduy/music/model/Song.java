package com.vuongvanduy.music.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Song implements Serializable {

    private String name;
    private String singer;
    private int image;
    private int resource;

    public Song(String name, String singer, int image, int resource) {
        this.name = name;
        this.singer = singer;
        this.image = image;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    @NonNull
    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", singer='" + singer + '\'' +
                ", image=" + image +
                ", resource=" + resource +
                '}';
    }
}
