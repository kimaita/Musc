package com.kimaita.musc;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kimaita.musc.DAOs.PlayRecordsDAO;
import com.kimaita.musc.DAOs.SessionsDAO;
import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.DashboardEntry;
import com.kimaita.musc.models.PlayRecord;
import com.kimaita.musc.models.PlaySession;
import com.kimaita.musc.models.RecordDets;
import com.kimaita.musc.models.Song;

import java.util.List;

public class MusicRepository {

    public final PlayRecordsDAO mRecordsDAO;
    private final SessionsDAO mSessionsDAO;
    private final LiveData<List<DashboardEntry>> topArtistsList;
    private final LiveData<List<DashboardEntry>> topSongsList;
    private final LiveData<Long> totalPlayTime;
    private final LiveData<List<Song>> offDeviceSongs;
    private final LiveData<List<Artist>> offDeviceArtists;


    public MusicRepository(Application application) {
        MusicRoomDatabase db = MusicRoomDatabase.getDatabase(application);
        mRecordsDAO = db.recordsDAO();
        mSessionsDAO = db.sessionsDAO();
        topArtistsList = mRecordsDAO.getTopArtists();
        topSongsList = mRecordsDAO.getTopSongs();
        totalPlayTime = mRecordsDAO.getTotalPlayTime();
        offDeviceSongs = mRecordsDAO.getOffDeviceSongs();
        offDeviceArtists = mRecordsDAO.getOffDeviceArtists();
    }

    public LiveData<List<DashboardEntry>> getTopSongsList() {
        return topSongsList;
    }

    public LiveData<List<DashboardEntry>> getTopArtistsList() {
        return topArtistsList;
    }

    public LiveData<Long> getTotalPlayTime() {
        return totalPlayTime;
    }

    public LiveData<List<Song>> getOffDeviceSongs() {
        return offDeviceSongs;
    }

    public LiveData<List<Artist>> getOffDeviceArtists() {
        return offDeviceArtists;
    }

    public LiveData<List<DashboardEntry>> getArtistRecords(String artist) {

        return mRecordsDAO.getArtistRecords(artist);
    }

    public LiveData<List<RecordDets>> getSongPlayHistory(Long id) {
        return mRecordsDAO.getSongPlayHistory(id);
    }

    public LiveData<List<DashboardEntry>> getArtistRecordsByID(Long[] ids) {
        return mRecordsDAO.getArtistRecordsbyID(ids);
    }

    public LiveData<List<Long>> getArtistSongIDs(String artist) {
        return mRecordsDAO.getArtistSongIDs(artist);
    }

    public LiveData<Long> getSongPlayTime(Long id) {
        return mRecordsDAO.getSongPlayTime(id);
    }

    public void insertPlayRecord(PlayRecord record) {
        MusicRoomDatabase.databaseWriteExecutor.execute(() -> mRecordsDAO.insert(record));
    }

    public void insertPlayRecords(PlayRecord... record) {
        MusicRoomDatabase.databaseWriteExecutor.execute(() -> mRecordsDAO.insertRecords(record));
    }


    public void insertSession(PlaySession session) {
        MusicRoomDatabase.databaseWriteExecutor.execute(() -> mSessionsDAO.insertSession(session));
    }

    public void insertSession(PlaySession... sessions) {
        MusicRoomDatabase.databaseWriteExecutor.execute(() -> mSessionsDAO.insertSession(sessions));
    }

    public void insertArtist(Artist artist) {
        MusicRoomDatabase.databaseWriteExecutor.execute(() -> mRecordsDAO.insertArtist(artist));
    }

    public void insertArtists(Artist... artists) {
        MusicRoomDatabase.databaseWriteExecutor.execute(() -> mRecordsDAO.insertArtists(artists));
    }

}
