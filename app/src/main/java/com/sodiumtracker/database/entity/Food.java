package com.sodiumtracker.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.sodiumtracker.database.utils.DateConverter;

import java.util.Date;

@Entity
@TypeConverters(DateConverter.class)
public class Food {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "amount")
    public int amount;

    @ColumnInfo(name = "date")
    public Date date;


}
