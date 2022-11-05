package com.kimaita.musc.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kimaita.musc.MusicRepository;
import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.DashboardEntry;
import com.kimaita.musc.models.PlayRecord;
import com.kimaita.musc.models.PlaySession;
import com.kimaita.musc.models.RecordDets;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final MusicRepository mRepository;
    private final LiveData<List<DashboardEntry>> topSongsList;
    private final LiveData<List<DashboardEntry>> topArtistsList;
    private final LiveData<Long> totalPlayTime;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MusicRepository(application);
        topSongsList = mRepository.getTopSongsList();
        topArtistsList = mRepository.getTopArtistsList();
        totalPlayTime = mRepository.getTotalPlayTime();

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

    public LiveData<List<DashboardEntry>> getArtistRecords(String artiste) {
        return mRepository.getArtistRecords(artiste);
    }

    public LiveData<List<RecordDets>> getSongPlayHistory(Long id){
        return mRepository.getSongPlayHistory(id);
    }

    public void insertRecord(PlayRecord record) {
        mRepository.insertPlayRecord(record);
    }

    public void insertRecords(PlayRecord... record) {
        mRepository.insertPlayRecords(record);
    }

    public void insertSessions(PlaySession... sessions) {
        mRepository.insertSession(sessions);
    }

    public void insertArtists(Artist... artists) {
        mRepository.insertArtists(artists);
    }

}