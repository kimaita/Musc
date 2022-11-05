package com.kimaita.musc.ui.music.songs;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.kimaita.musc.MusicRepository;
import com.kimaita.musc.R;
import com.kimaita.musc.SpotifyCredentialsHandler;
import com.kimaita.musc.SpotifyUtils;
import com.kimaita.musc.models.RecordDets;
import com.kimaita.musc.models.Song;

import java.util.List;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.Track;

public class SongInfoViewModel extends AndroidViewModel {

    private final MusicRepository mRepository;
    private MutableLiveData<Song> mSong;
    private MutableLiveData<Song> songMinimal;
    private final Application mApplication;
    private final SpotifyUtils spotifyUtils;
    private final String TAG = "SongInfoViewModel";

    public SongInfoViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
        mRepository = new MusicRepository(application);
        spotifyUtils = new SpotifyUtils(application.getApplicationContext());
    }

    public LiveData<List<RecordDets>> getRecords(Long id) {
        return mRepository.getSongPlayHistory(id);
    }

    public LiveData<Long> getSongPlaytime(Long id) {
        return mRepository.getSongPlayTime(id);
    }

    public LiveData<Song> getSongTitleArt(Long id) {
        if (songMinimal == null) {
            songMinimal = new MutableLiveData<>();
            songTitleArt(mApplication, id);
        }
        return songMinimal;
    }

    private void songTitleArt(Application application, Long id) {
        Song song = new Song();
        ContentResolver musicResolver = application.getContentResolver();
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        String[] projection = {MediaStore.Audio.Media.TITLE};
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(application.getApplicationContext(), contentUri);
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null) {
            song.setAlbumArt(BitmapFactory.decodeByteArray(art, 0, art.length));
        }
        retriever.release();
        Cursor musicCursor = musicResolver.query(contentUri, projection, null, null, null);
        if (musicCursor == null) {
            // query failed, handle error.
            Log.w("MusicCursor", "Couldn't initialise cursor.");
        } else if (!musicCursor.moveToFirst()) {
            // no media on the device
            Log.w("MusicCursor", "Couldn't find: " + id);
        } else {
            song.setSavedID(id);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            if (musicCursor.moveToFirst()) {
                song.setSongTitle(musicCursor.getString(titleColumn));
            }
        }
        songMinimal.postValue(song);
        assert musicCursor != null;
        musicCursor.close();

    }

    public LiveData<Song> getmSong(Long id) {
        if (mSong == null) {
            mSong = new MutableLiveData<>();
            getSongDetails(mApplication, id);
        }
        return mSong;
    }

    private void getSongDetails(Application application, Long id) {
        Song song = new Song();
        ContentResolver musicResolver = application.getContentResolver();
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        String[] projection = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM};
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(application.getApplicationContext(), contentUri);
        song.setSongGenre(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        retriever.release();
        Cursor musicCursor = musicResolver.query(contentUri, projection, null, null, null);
        if (musicCursor == null) {
            // query failed, handle error.
            Log.w("MusicCursor", "Couldn't initialise cursor.");
        } else if (!musicCursor.moveToFirst()) {
            // no media on the device
            Log.w("MusicCursor", "Couldn't find: " + id);
        } else {
            song.setSavedID(id);
            int lengthColumn = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            if (musicCursor.moveToFirst()) {
                song.setSongTitle(musicCursor.getString(titleColumn));
                song.setSongLength(musicCursor.getLong(lengthColumn));
                song.setSongArtist(musicCursor.getString(artistColumn));
                /*if (SpotifyCredentialsHandler.getToken(application.getApplicationContext()) != null) {
                    Track track = searchTrackSpotify(application, song.getSongTitle(), song.getSongArtist());
                    if (track != null) {
                        song.setSpotifyID(track.id);
                        song.setSpotifyUri(track.uri);
                        AudioFeaturesTrack features = getAudioFeaturesSpotify(track.id);
                        if (features != null) {
                            song.setValence(features.valence);
                            song.setEnergy(features.energy);
                            song.setDanceability(features.danceability);
                            song.setInstrumentalness(features.instrumentalness);
                            song.setSpeechiness(features.speechiness);
                        }
                    }
                }*/
                song.setSongAlbum(musicCursor.getString(albumColumn));
            }
        }
        mSong.postValue(song);
        assert musicCursor != null;
        musicCursor.close();

    }

    private Track searchTrackSpotify(Application application, String title, String artist) {
        String query = application.getResources().getString(R.string.search_query_filters, title, artist);
        Log.i(TAG, "Fetching for: " + query);
        Track item = spotifyUtils.searchTrack(query);
        if (item != null) {
            Log.i(TAG, "Search result: " + item.name + " uri: " + item.uri);
        }
        return item;
    }

    private Track getTrackSpotify(String id) {
        Log.i(TAG, "Fetching track ID: " + id);
        Track item = spotifyUtils.getTrack(id);
        if (item != null) {
            Log.i(TAG, "Search result: " + item.name + " uri: " + item.uri);
        }
        return item;
    }

    private AudioFeaturesTrack getAudioFeaturesSpotify(String id) {
        Log.i(TAG, "Fetching Audio Features: " + id);
        AudioFeaturesTrack featuresTrack = spotifyUtils.getTrackAudioFeatures(id);
        if (featuresTrack != null) {
            Log.i(TAG, "Search Result: " + featuresTrack.uri + " link: " + featuresTrack.analysis_url);
        }
        return featuresTrack;
    }


}