package com.kimaita.musc;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kimaita.musc.DAOs.PlayRecordsDAO;
import com.kimaita.musc.DAOs.SessionsDAO;
import com.kimaita.musc.models.Album;
import com.kimaita.musc.models.Artist;
import com.kimaita.musc.models.PlayRecord;
import com.kimaita.musc.models.PlaySession;
import com.kimaita.musc.models.Song;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Album.class, Artist.class, PlayRecord.class, PlaySession.class, Song.class},
        version = 1)
@TypeConverters({Converters.class})
public abstract class MusicRoomDatabase extends RoomDatabase {

    public abstract PlayRecordsDAO recordsDAO();

    public abstract SessionsDAO sessionsDAO();

    private static volatile MusicRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 6;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MusicRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MusicRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MusicRoomDatabase.class, "music_database")
                            //.addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `songs` ADD COLUMN albumID INTEGER");
        }
    };
}
