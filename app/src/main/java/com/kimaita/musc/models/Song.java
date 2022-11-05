package com.kimaita.musc.models;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "songs", primaryKeys = {"songTitle", "songArtist"})
public class Song implements Serializable {

    Long savedID;
    @NonNull
    String songTitle;
    Long songLength;
    @NonNull
    String songArtist;
    String songAlbum;
    Long albumID;
    String songGenre;
    Long dateAdded;
    Integer songYear;
    Bitmap albumArt;
    String spotifyID;
    String spotifyUri;
    String spotifyReleaseDate;
    String analysisUri;
    String spotifyArtUri;
    String spotifyAlbumID;
    Float acousticness;
    Float danceability;
    Float energy;
    Float instrumentalness;
    Float liveness;
    Float loudness;
    Float speechiness;
    Float valence;

    public Long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(Long albumID) {
        this.albumID = albumID;
    }

    public String getSpotifyAlbumID() {
        return spotifyAlbumID;
    }

    public void setSpotifyAlbumID(String spotifyAlbumID) {
        this.spotifyAlbumID = spotifyAlbumID;
    }

    public String getSpotifyArtUri() {
        return spotifyArtUri;
    }

    public void setSpotifyArtUri(String spotifyArtUri) {
        this.spotifyArtUri = spotifyArtUri;
    }

    public Long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Long getSavedID() {
        return savedID;
    }

    public void setSavedID(Long savedID) {
        this.savedID = savedID;
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

    public String getSongAlbum() {
        return songAlbum;
    }

    public void setSongAlbum(String songAlbum) {
        this.songAlbum = songAlbum;
    }

    public String getSongGenre() {
        return songGenre;
    }

    public void setSongGenre(String songGenre) {
        this.songGenre = songGenre;
    }

    public void setSongLength(Long songLength) {
        this.songLength = songLength;
    }

    public Long getSongLength() {
        return songLength;
    }

    public Integer getSongYear() {
        return songYear;
    }

    public void setSongYear(Integer songYear) {
        this.songYear = songYear;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }


    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getSpotifyUri() {
        return spotifyUri;
    }

    public void setSpotifyUri(String spotifyUri) {
        this.spotifyUri = spotifyUri;
    }

    public String getSpotifyReleaseDate() {
        return spotifyReleaseDate;
    }

    public void setSpotifyReleaseDate(String spotifyReleaseDate) {
        this.spotifyReleaseDate = spotifyReleaseDate;
    }

    public String getAnalysisUri() {
        return analysisUri;
    }

    public void setAnalysisUri(String analysisUri) {
        this.analysisUri = analysisUri;
    }

    public Float getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(Float acousticness) {
        this.acousticness = acousticness;
    }

    public Float getDanceability() {
        return danceability;
    }

    public void setDanceability(Float danceability) {
        this.danceability = danceability;
    }

    public Float getEnergy() {
        return energy;
    }

    public void setEnergy(Float energy) {
        this.energy = energy;
    }

    public Float getInstrumentalness() {
        return instrumentalness;
    }

    public void setInstrumentalness(Float instrumentalness) {
        this.instrumentalness = instrumentalness;
    }

    public Float getLiveness() {
        return liveness;
    }

    public void setLiveness(Float liveness) {
        this.liveness = liveness;
    }

    public Float getLoudness() {
        return loudness;
    }

    public void setLoudness(Float loudness) {
        this.loudness = loudness;
    }

    public Float getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(Float speechiness) {
        this.speechiness = speechiness;
    }

    public Float getValence() {
        return valence;
    }

    public void setValence(Float valence) {
        this.valence = valence;
    }
}
