package com.example.workit.romdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diet.class, DietNames.class}, version = 5)
public abstract class DietDb extends RoomDatabase {
    public abstract DieDao dieDao();
    public static DietDb INSTANCE;

    public static DietDb getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),DietDb.class,"Diet_DB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
