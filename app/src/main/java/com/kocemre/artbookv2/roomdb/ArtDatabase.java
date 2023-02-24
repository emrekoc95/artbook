package com.kocemre.artbookv2.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kocemre.artbookv2.model.Art;

@Database(entities = {Art.class},version = 1)
public abstract class ArtDatabase extends RoomDatabase {
    public abstract ArtDao artDao();
}
