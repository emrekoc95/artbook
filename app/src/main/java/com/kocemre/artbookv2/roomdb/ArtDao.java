package com.kocemre.artbookv2.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.kocemre.artbookv2.model.Art;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.flow.Flow;

@Dao
public interface ArtDao {

    @Query("SELECT * FROM Art")
    Flowable<List<Art>> getAll();

    @Query("SELECT * FROM Art WHERE id LIKE :idInput")
    Flowable<Art> getById(int idInput);

    @Insert
    Completable insert(Art art);

    @Delete
    Completable delete(Art art);

    /*@Query("SELECT * FROM Art")
    List<Art> getAll();

    @Query("SELECT * FROM Art WHERE id LIKE :idInput")
    Art getById(int idInput);

    @Insert
    void insert(Art art);

    @Delete
    void delete(Art art);*/



}
