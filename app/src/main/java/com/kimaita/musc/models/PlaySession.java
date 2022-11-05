package com.kimaita.musc.models;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "sessions")
public class PlaySession implements Serializable {

    @NonNull
    @PrimaryKey()
    private String sessionID;

    @ColumnInfo(name = "startTime")
    private Long startTime;

    @ColumnInfo(name = "endTime")
    private Long endTime;

    @ColumnInfo(name = "duration")
    private long sessionLength;

    @NonNull
    private String player;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @NonNull
    public String getPlayer() {
        return player;
    }

    public void setPlayer(@NonNull String player) {
        this.player = player;
    }

    public long getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(long sessionLength) {
        this.sessionLength = sessionLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaySession that = (PlaySession) o;
        return sessionID.equals(that.sessionID) && player.equals(that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionID, player);
    }
}
