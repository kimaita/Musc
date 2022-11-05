package com.kimaita.musc.DAOs;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.kimaita.musc.models.PlaySession;

@Dao
public interface SessionsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSession(PlaySession session);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSession(PlaySession... sessions);


}
