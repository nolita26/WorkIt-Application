package com.example.workit.romdb;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Workout")
public class Workout implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "workout_type")
    public String workoutType;

    @ColumnInfo(name = "number_of_set")
    public String numberOfSets;

    @ColumnInfo(name = "reps")
    public String repeatitions;

    @ColumnInfo(name = "date_of_workout")
    public String dateOfWorkout;

    @ColumnInfo(name = "duration_of_workout")
    public String durationOfWorkout;

    @ColumnInfo(name = "user")
    public String user;

    @NonNull @ColumnInfo(name = "workout_name")
    public String workoutName;

}

