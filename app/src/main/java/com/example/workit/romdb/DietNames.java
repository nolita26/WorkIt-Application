package com.example.workit.romdb;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "Diet_Names")
public class DietNames implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "user")
    public String user;

    @ColumnInfo(name = "diet_name")
    public String dietName;
}
