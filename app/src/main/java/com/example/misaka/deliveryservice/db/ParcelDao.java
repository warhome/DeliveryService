package com.example.misaka.deliveryservice.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Dao
public interface ParcelDao {

    @Query("SELECT * FROM parcels")
    Flowable<List<Parcel>> getAll();

    @Query("DELETE FROM parcels")
    void nukeTable();

    @Query("SELECT * FROM parcels WHERE status like 'Active'")
    Flowable<List<Parcel>> getActive();

    @Query("SELECT * FROM parcels WHERE status like 'Completed'")
    Flowable<List<Parcel>> getCompleted();

    @Query("SELECT * FROM parcels WHERE id = :id")
    Flowable<Parcel> getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Parcel parcel);

    @Delete
    void delete(Parcel parcel);
}
