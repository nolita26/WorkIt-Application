package com.example.workit.romdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Diet")
public class Diet implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "food_name")
    public String foodName;

    @ColumnInfo(name = "cal_count")
    public String calCount;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "date_of_diet")
    public String dateOfDiet;

    @ColumnInfo(name = "user")
    public String user;

    @ColumnInfo(name = "diet_name")
    public String dietName;
}
