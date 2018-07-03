package com.example.misaka.deliveryservice.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Parcel.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public  abstract ParcelDao lessonDao();
}
