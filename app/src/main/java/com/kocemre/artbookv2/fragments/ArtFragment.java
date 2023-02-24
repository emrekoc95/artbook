package com.kocemre.artbookv2.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.kocemre.artbookv2.R;
import com.kocemre.artbookv2.model.Art;
import com.kocemre.artbookv2.roomdb.ArtDao;
import com.kocemre.artbookv2.roomdb.ArtDatabase;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ArtFragment extends Fragment {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    ArtDatabase db;
    ArtDao artDao;

    ShapeableImageView selectedImageView;
    TextView artNameTextView;
    TextView artistNameTextView;
    TextView notesTextView;


    public ArtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getContext(), ArtDatabase.class, "Art").allowMainThreadQueries().build();
        artDao = db.artDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_art, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artNameTextView = view.findViewById(R.id.artNameTextView);
        artistNameTextView = view.findViewById(R.id.artistNameTextView);
        notesTextView = view.findViewById(R.id.notesTextView);
        selectedImageView = view.findViewById(R.id.selectedImageView);

        if (getArguments() != null) {

            int id = ArtFragmentArgs.fromBundle(getArguments()).getId();

            compositeDisposable.add(artDao.getById(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ArtFragment.this::getArt));

        }

    }

    public void getArt(Art art){
        String artName = art.getArtName();
        String artistName = art.getArtistName();
        String notes = art.getNotes();

        byte[] bArray = art.getByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);

        artNameTextView.setText(artName);
        artistNameTextView.setText(artistName);
        notesTextView.setText(notes);
        selectedImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}