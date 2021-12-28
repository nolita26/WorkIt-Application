package com.example.workit.romdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DieDao {
    @Query("Select * From Diet")
    List<Diet> getAllDiet();

    @Query("Select * from Diet where date_of_diet = :date")
    List<Diet> getDietForDate(String date);

    @Query("Select * from Diet where user = :user")
    List<Diet> getDietForUser(String user);

    @Query("Select * from Diet where user = :user AND date_of_diet = :date")
    List<Diet> getDietForUserAndDate(String user, String date);

    @Query("Select * from Diet where user = :user AND date_of_diet = :date AND diet_name = :dietName")
    List<Diet> getDietForUserAndDateAndName(String user, String date, String dietName);

    @Insert
    void insertDiet(Diet diet);

    @Insert
    void insertDietNames(DietNames dietNames);

    @Query("Select * From diet_names where user = :user")
    List<DietNames> getDietNames(String user);

    @Query("Select * From diet_names ")
    List<DietNames> getDietNames1();

    @Delete
    void delete(Diet diet);

    @Query("DELETE FROM Diet")
    public void nukeTableDiet();

    @Query("DELETE FROM Diet_Names")
    public void nukeTableDietNames();
    
}
