package com.kimaita.musc.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "albums")
public class Album {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int albumID;

    @ColumnInfo(name = "title", defaultValue = "Unknown")
    private String albumTitle;

    @ColumnInfo(name = "year")
    private int albumYear;
    private Long savedAlbumID;
    private int songCount;
    private String artist;
    private byte[] albumArt;
    private Uri albumUri;

    public Uri getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(Uri albumUri) {
        this.albumUri = albumUri;
    }

    public byte[] getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(byte[] albumArt) {
        this.albumArt = albumArt;
    }

    public Long getSavedAlbumID() {
        return savedAlbumID;
    }

    public void setSavedAlbumID(Long savedAlbumID) {
        this.savedAlbumID = savedAlbumID;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getAlbumID() {
        return albumID;
    }

    public void setAlbumID(int albumID) {
        this.albumID = albumID;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public int getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(int albumYear) {
        this.albumYear = albumYear;
    }
}
