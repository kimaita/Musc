package com.kimaita.musc.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.DashboardEntry;
import com.kimaita.musc.models.PlayRecord;
import com.kimaita.musc.models.RecordDets;
import com.kimaita.musc.models.Song;

import java.util.List;

@Dao
public interface PlayRecordsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PlayRecord record);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRecords(PlayRecord... record);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertArtist(Artist artist);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertArtists(Artist... artists);

    @Delete
    void delete(PlayRecord record);

    @Update()
    void update(PlayRecord record);


    @Query("DELETE FROM records where songArtist = 'Unknown Artist' OR songArtist ='' ")
    void delete();


    @Query("SELECT * FROM records")
    LiveData<List<PlayRecord>> getAllRecords();

    @Query("SELECT songID as identifier, records.songTitle as title, songArtist as name, count(records.songTitle)as plays, sum(duration) as duration FROM records " +
            " WHERE duration>30000 GROUP BY title, name ORDER BY plays desc, duration DESC LIMIT 8")
    LiveData<List<DashboardEntry>> getTopSongs();

    @Query("SELECT songArtist as name, count(DISTINCT songTitle)as plays, sum(duration) as duration FROM records WHERE duration>30000 GROUP BY songArtist ORDER BY  duration DESC, plays DESC LIMIT 8")
    LiveData<List<DashboardEntry>> getTopArtists();

    @Query("SELECT sum(duration) as playtime FROM records")
    LiveData<Long> getTotalPlayTime();

    @Query("SELECT records.songArtist as name, records.songTitle as title, sum(duration) as duration, count(records.songTitle) as plays FROM records " +
            "WHERE records.songArtist LIKE :artist GROUP by records.songTitle ORDER BY plays DESC")
    LiveData<List<DashboardEntry>> getArtistRecords(String artist);

    @Query("SELECT sum(duration) as duration FROM records WHERE songID = :id")
    LiveData<Long> getSongPlayTime(Long id);

    @Query("SELECT records.startTime as startTime, records.duration as duration, sessions.player as packageName FROM records INNER JOIN sessions " +
            "ON records.mediaSession = sessions.sessionID WHERE songID = :id")
    LiveData<List<RecordDets>> getSongPlayHistory(Long id);

    @Query("SELECT records.songTitle as title, sum(duration) as duration, count(records.songTitle) AS plays FROM records WHERE records.songID IN (:ids) GROUP by records.songID")
    LiveData<List<DashboardEntry>> getArtistRecordsbyID(Long[] ids);

    @Query("SELECT DISTINCT songID FROM records WHERE songArtist LIKE :artist")
    LiveData<List<Long>> getArtistSongIDs(String artist);

    @Query("SELECT DISTINCT records.songTitle, records.songArtist FROM records WHERE records.songID IS NULL")
    LiveData<List<Song>> getOffDeviceSongs();

    @Query("SELECT COUNT(DISTINCT songTitle) AS artistSongCount, songArtist AS name from records " +
            "WHERE records.songID IS NULL GROUP BY name")
    LiveData<List<Artist>> getOffDeviceArtists();
}
