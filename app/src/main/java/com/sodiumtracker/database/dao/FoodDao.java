package com.sodiumtracker.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sodiumtracker.database.entity.Food;

import java.util.List;

@Dao
public interface FoodDao {

    @Insert
    void insertAll(Food... foods);

    @Update
    public void updateFoods(Food... users);

    @Delete
    void delete(Food food);

    @Query("SELECT * FROM food WHERE id = :id")
    Food getOne(int id);

    @Query("SELECT * FROM food")
    List<Food> getAll();

    @Query("SELECT id,name,SUM(amount) as amount,date  FROM food GROUP BY date(date/1000, 'unixepoch')")
    List<Food> getAllGroupByDates();

    @Query("SELECT * FROM food WHERE date BETWEEN :from AND :to")
    List<Food> getByDate(long from, long to);

    @Query("SELECT SUM(amount) FROM food WHERE date BETWEEN :from AND :to")
    int getTotalAmountByDate(long from, long to);

}
