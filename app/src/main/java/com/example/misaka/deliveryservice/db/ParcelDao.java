package com.example.misaka.deliveryservice.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ParcelDao {

    @Query("SELECT * FROM parcels")
    List<Parcel> getAll();

    @Query("DELETE FROM parcels")
    void nukeTable();

    @Query("SELECT * FROM parcels WHERE status like 'Active'")
    List<Parcel> getActive();

    @Query("SELECT * FROM parcels WHERE status like 'Completed'")
    List<Parcel> getCompleted();

    @Query("SELECT * FROM parcels WHERE id = :id")
    Parcel getById(long id);

    @Insert
    void insert(Parcel parcel);

    @Update
    void update(Parcel parcel);

    @Delete
    void delete(Parcel parcel);
}
