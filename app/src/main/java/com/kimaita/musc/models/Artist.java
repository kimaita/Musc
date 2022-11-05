package com.kimaita.musc.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "artists")
public class Artist implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "name")
    private String artistName;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    private Integer artistSongCount;
    private Integer albumCount;
    private Long savedArtistID;
    private byte[] artistPic;

    public Integer getArtistSongCount() {
        return artistSongCount;
    }

    public void setArtistSongCount(Integer songCount) {
        this.artistSongCount = songCount;
    }

    public Integer getAlbumCount() {
        return albumCount;
    }

    public void setAlbumCount(Integer albumCount) {
        this.albumCount = albumCount;
    }

    public Long getSavedArtistID() {
        return savedArtistID;
    }

    public void setSavedArtistID(Long savedArtistID) {
        this.savedArtistID = savedArtistID;
    }

    public byte[] getArtistPic() {
        return artistPic;
    }

    public void setArtistPic(byte[] artistPic) {
        this.artistPic = artistPic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return artistName.equals(artist.artistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artistName);
    }
}
