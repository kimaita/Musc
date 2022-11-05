package com.kimaita.musc.ui.music;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kimaita.musc.MusicRepository;
import com.kimaita.musc.models.Album;
import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<Song>> onDeviceSongList;
    private final LiveData<List<Song>> offDeviceSongList;
    private MutableLiveData<ArrayList<Album>> onDeviceAlbumList;
    private MutableLiveData<ArrayList<Artist>> onDeviceArtistList;
    private final LiveData<List<Artist>> offDeviceArtistList;
    private final MutableLiveData<Boolean> noMusic = new MutableLiveData<>();
    private final MutableLiveData<Boolean> noAlbums = new MutableLiveData<>();
    private final MutableLiveData<Boolean> noArtists = new MutableLiveData<>();
    private final Application mApplication;

    public MusicViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        MusicRepository mRepository = new MusicRepository(application);
        offDeviceSongList = mRepository.getOffDeviceSongs();
        offDeviceArtistList = mRepository.getOffDeviceArtists();
    }

    public MutableLiveData<Boolean> getNoMusic() {
        return noMusic;
    }

    public MutableLiveData<Boolean> getNoAlbums() {
        return noAlbums;
    }

    public MutableLiveData<Boolean> getNoArtists() {
        return noArtists;
    }

    public LiveData<ArrayList<Song>> getOnDeviceSongList() {
        if (onDeviceSongList == null) {
            onDeviceSongList = new MutableLiveData<>();
            getSongList(mApplication);
        }
        return onDeviceSongList;
    }

    public LiveData<ArrayList<Album>> getOnDeviceAlbumList() {
        if (onDeviceAlbumList == null) {
            onDeviceAlbumList = new MutableLiveData<>();
            getAlbumList(mApplication);
        }
        return onDeviceAlbumList;
    }

    public LiveData<ArrayList<Artist>> getOnDeviceArtistList() {
        if (onDeviceArtistList == null) {
            onDeviceArtistList = new MutableLiveData<>();
            getArtistsList(mApplication);
        }
        return onDeviceArtistList;
    }

    public LiveData<List<Song>> getOffDeviceSongList() {
        return offDeviceSongList;
    }

    public LiveData<List<Artist>> getOffDeviceArtistList() {
        return offDeviceArtistList;
    }

    private void getSongList(Application application) {
        ArrayList<Song> songArrayList = new ArrayList<>();
        ContentResolver musicResolver = application.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] musicProjection = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor musicCursor = musicResolver.query(musicUri, musicProjection, selection, null, null);

        if (musicCursor != null) {
            if (!musicCursor.moveToFirst()) {
                noMusic.postValue(true);
            } else {
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int lengthColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int albumColumnID = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                while (musicCursor.moveToNext()) {
                    Song song = new Song();
                    song.setSavedID(musicCursor.getLong(idColumn));
                    song.setSongTitle(musicCursor.getString(titleColumn));
                    song.setSongArtist(musicCursor.getString(artistColumn));
                    song.setSongLength(musicCursor.getLong(lengthColumn));
                    song.setAlbumID(musicCursor.getLong(albumColumnID));
                    songArrayList.add(song);
                }
            }
        } else {
            // query failed, handle error.
        }
        assert musicCursor != null;
        onDeviceSongList.postValue(songArrayList);
        musicCursor.close();
    }

    private void getAlbumList(Application application) {
        ArrayList<Album> albumArrayList = new ArrayList<>();
        ContentResolver musicResolver = application.getContentResolver();
        Uri albumsUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] albumsProjection = {MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.NUMBER_OF_SONGS,
                MediaStore.Audio.Albums.ARTIST};
        Cursor albumsCursor = musicResolver.query(albumsUri, albumsProjection, null, null, null);
        if (albumsCursor != null) {
            if (!albumsCursor.moveToFirst()) {
                noAlbums.postValue(true);
            } else {
                int columnID = albumsCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID);
                int columnTitle = albumsCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM);
                int columnSongs = albumsCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
                int columnArtist = albumsCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST);

                while (albumsCursor.moveToNext()) {
                    Album album = new Album();
                    album.setSavedAlbumID(albumsCursor.getLong(columnID));
                    album.setAlbumTitle(albumsCursor.getString(columnTitle));
                    album.setSongCount(albumsCursor.getInt(columnSongs));
                    album.setArtist(albumsCursor.getString(columnArtist));
                    try {
                        Uri albumUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumsCursor.getLong(columnID));
                        album.setAlbumUri(albumUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    albumArrayList.add(album);
                }
            }
        } else {
            // query failed, handle error.
        }
        assert albumsCursor != null;
        onDeviceAlbumList.postValue(albumArrayList);
        albumsCursor.close();
    }

    private void getArtistsList(Application application) {
        ArrayList<Artist> artistArrayList = new ArrayList<>();
        ContentResolver musicResolver = application.getContentResolver();
        Uri artistsUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] artistsProjection = {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS};
        Cursor artistCursor = musicResolver.query(artistsUri, artistsProjection, null, null, null);

        if (artistCursor != null) {
            if (!artistCursor.moveToFirst()) {
                noArtists.postValue(true);
            } else {
                int columnID = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID);
                int columnArtist = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                int columnSongs = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
                int columnAlbums = artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
                while (artistCursor.moveToNext()) {
                    Artist artist = new Artist();
                    artist.setSavedArtistID(artistCursor.getLong(columnID));
                    artist.setArtistName(artistCursor.getString(columnArtist));
                    artist.setArtistSongCount(artistCursor.getInt(columnSongs));
                    artist.setAlbumCount(artistCursor.getInt(columnAlbums));
                    artistArrayList.add(artist);
                }
            }
        } else {
            // query failed, handle error.
        }
        assert artistCursor != null;
        onDeviceArtistList.postValue(artistArrayList);
        artistCursor.close();
    }

}