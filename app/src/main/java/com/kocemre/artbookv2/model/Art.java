package com.kocemre.artbookv2.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

@Entity
public class Art {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="artName")
    public String artName;

    @ColumnInfo(name="artistName")
    public String artistName;

    @ColumnInfo(name="notes")
    public String notes;

    @ColumnInfo(name="byteArray")
    public byte[] byteArray;


    public Art(String artName,String artistName,String notes,byte[] byteArray){
        this.artName = artName;
        this.artistName = artistName;
        this.notes = notes;
        this.byteArray = byteArray;
    }

    public Bitmap getBitmap(byte[]byteArray){

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        return bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtName() {
        return artName;
    }

    public void setArtName(String artName) {
        this.artName = artName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }
}
