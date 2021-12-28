package com.example.workit.romdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface WorkoutDoa {

    @Query("Select * From Workout")
    List<Workout> getAllWorkout();

    @Query("Select * From Workout where date_of_workout =:date")
    List<Workout> getWorkoutOfDate(String date);

    @Query("Select * from Workout where user = :user")
    List<Workout> getWorkoutForUser(String user);

    @Query("Select * from Workout where user = :user AND date_of_workout = :date")
    List<Workout> getWorkoutForUserAndDate(String user, String date);

    @Insert
    void insertWorkout(Workout workout);

    @Query("Select * from Workout where workout_name= :workoutName AND date_of_workout = :date AND user= :user ")
    List<Workout> getWorkoutForNameAndDate(String workoutName, String date, String user);

    @Delete
    void delete(Workout workout);

    @Insert
    void insertTable(WorkoutNames names);

    @Query("Select * from WorkoutNames")
    List<WorkoutNames> getWorkoutNames();

    @Query("DELETE FROM WorkoutNames")
    void nukeWorkoutNames();

    @Query("DELETE FROM Workout")
    void nukeWorkout();

    @Query("DELETE FROM Workout WHERE workout_name = :name")
    public void deleteByUserId(String name);

}
