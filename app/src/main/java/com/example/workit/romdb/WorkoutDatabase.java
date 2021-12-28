package com.example.workit.romdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Workout.class, WorkoutNames.class}, version = 9)
public abstract class WorkoutDatabase extends RoomDatabase {
    public abstract WorkoutDoa workoutDoa();
    public static WorkoutDatabase INSTANCE;

    public static WorkoutDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WorkoutDatabase.class,"Workout_DB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}
