package com.example.workit.romdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Map;

@Entity(tableName = "WorkoutNames")
public class WorkoutNames implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "workout_name")
    public String workoutName;

    @ColumnInfo(name = "user")
    public String user;

}
