package com.kimaita.musc.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "records", primaryKeys = {"startTime", "songTitle"})
public class PlayRecord implements Serializable  {

    @ColumnInfo(name = "songID")
    private Long recordSongID;

    private String recordSpotifyID;

    @NonNull
    private String songTitle;

    private String songArtist;

    @NonNull
    @ColumnInfo(name = "startTime")
    private Long startTime;

    @ColumnInfo(name = "endTime")
    private Long endTime;

    @ColumnInfo(name = "duration")
    private Long duration;

    @NonNull
    private String mediaSession;

    public String getMediaSession() {
        return mediaSession;
    }

    public void setMediaSession(String mediaSession) {
        this.mediaSession = mediaSession;
    }

    public Long getRecordSongID() {
        return recordSongID;
    }

    public void setRecordSongID(Long recordSongID) {
        this.recordSongID = recordSongID;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @NonNull
    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(@NonNull Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getRecordSpotifyID() {
        return recordSpotifyID;
    }

    public void setRecordSpotifyID(String recordSpotifyID) {
        this.recordSpotifyID = recordSpotifyID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayRecord that = (PlayRecord) o;
        return songTitle.equals(that.songTitle) && songArtist.equals(that.songArtist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songTitle, songArtist);
    }


    /*e target entity is specified via entity() then the parameters can be of arbitrary POJO types that will be interpreted as partial entities. For example:

 @Entity
 public class Playlist {
   @PrimaryKey(autoGenerate = true)
   long playlistId;
   String name;
   @ColumnInfo(defaultValue = "")
   String description
   @ColumnInfo(defaultValue = "normal")
   String category;
   @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
   String createdTime;
   @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
   String lastModifiedTime;
 }

 public class PlaylistCategory {
   long playlistId;
   String category;
   String lastModifiedTime
 }

 @Dao
 public interface PlaylistDao {
   @Update(entity = Playlist.class)
   public void updateCategory(PlaylistCategory... category);
 }*/
}
