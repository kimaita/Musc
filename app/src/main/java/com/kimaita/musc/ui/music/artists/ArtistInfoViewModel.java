package com.kimaita.musc.ui.music.artists;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kimaita.musc.MusicRepository;
import com.kimaita.musc.SpotifyUtils;
import com.kimaita.musc.models.DashboardEntry;

import java.util.ArrayList;
import java.util.List;

public class ArtistInfoViewModel extends AndroidViewModel {

    private final Application mApplication;
    private final MusicRepository mRepository;
    private MutableLiveData<ArrayList<DashboardEntry>> songList;
    private MutableLiveData<ArrayList<DashboardEntry>> albumList;
    private final SpotifyUtils spotifyUtils;
    private final String TAG = "ArtistInfoViewModel";

    public ArtistInfoViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        mRepository = new MusicRepository(application);
        spotifyUtils = new SpotifyUtils(application.getApplicationContext());
    }


    public MutableLiveData<ArrayList<DashboardEntry>> getAlbumList(Long id) {
        if (albumList == null) {
            albumList = new MutableLiveData<>();
            getArtistAlbums(mApplication, id);
        }
        return albumList;
    }

    public MutableLiveData<ArrayList<DashboardEntry>> getSongList(Long id, String name) {
        if (songList == null) {
            songList = new MutableLiveData<>();
            getArtistSongs(mApplication, id, name);
        }
        return songList;
    }

    private void getArtistSongs(Application application, Long id, String name) {
        ArrayList<Long> songIDList = new ArrayList<>();
        ContentResolver musicResolver = application.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] musicProjection = {MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.ARTIST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor musicCursor = musicResolver.query(musicUri, musicProjection, selection, selectionArgs, null);

        if (musicCursor != null) {
            if (!musicCursor.moveToFirst()) {

            } else {
                int idColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                while (musicCursor.moveToNext()) {
                    Long songID = musicCursor.getLong(idColumn);
                    songIDList.add(songID);
                }
            }
        } else {
            // query failed, handle error.
        }
        assert musicCursor != null;
        musicCursor.close();
        ArrayList<DashboardEntry> records;
        records = (ArrayList<DashboardEntry>) mRepository.getArtistRecordsByID(songIDList.toArray(new Long[0])).getValue();
        List<Long> playedIDs = mRepository.getArtistSongIDs(name).getValue();
        for (Long songID : songIDList) {
            assert playedIDs != null;
            if (!playedIDs.contains(songID)) {
                DashboardEntry unplayed = new DashboardEntry();
                unplayed.setIdentifier(songID);
                Uri contentUri = ContentUris.withAppendedId(
                        android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID);
                String[] projection = {MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION};
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(application.getApplicationContext(), contentUri);
                unplayed.setImage(retriever.getEmbeddedPicture());
                retriever.release();
                Cursor songCursor = musicResolver.query(contentUri, projection, null, null, null);
                if (songCursor == null) {
                    // query failed, handle error.
                    Log.w(TAG, "Couldn't initialise song cursor.");
                } else if (!songCursor.moveToFirst()) {
                    // no media on the device
                    Log.w(TAG, "Couldn't find: " + songID);
                } else {
                    int lengthColumn = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    int titleColumn = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    if (songCursor.moveToFirst()) {
                        unplayed.setTitle(songCursor.getString(titleColumn));
                        unplayed.setDuration(songCursor.getLong(lengthColumn));
                        unplayed.setPlays(0);
                        records.add(unplayed);
                    }
                }
                assert songCursor != null;
                songCursor.close();
            }
        }
        songList.postValue(records);
    }

    private void getArtistAlbums(Application mApplication, Long id) {

    }
}