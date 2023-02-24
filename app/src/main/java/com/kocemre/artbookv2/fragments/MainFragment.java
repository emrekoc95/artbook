package com.kocemre.artbookv2.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kocemre.artbookv2.R;
import com.kocemre.artbookv2.adapter.RecyclerViewAdapter;
import com.kocemre.artbookv2.model.Art;
import com.kocemre.artbookv2.roomdb.ArtDao;
import com.kocemre.artbookv2.roomdb.ArtDatabase;


import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;



public class MainFragment extends Fragment {
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    ArtDatabase db;
    ArtDao artDao;

    List<Art> artList;

    FloatingActionButton button;


    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = Room.databaseBuilder(getActivity().getApplicationContext(), ArtDatabase.class, "Art").build();
        artDao = db.artDao();

        //artList = artDao.getAll();





    }

    public void getArtList(List<Art> artList){

        recyclerViewAdapter = new RecyclerViewAdapter(this, artList);
        recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = view.findViewById(R.id.floatingActionButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(view);
            }
        });

        compositeDisposable.add(artDao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainFragment.this::getArtList));







    }

    public void add(View view) {
        NavDirections navDirections = MainFragmentDirections.actionMainFragmentToSelectionFragment();
        Navigation.findNavController(view).navigate(navDirections);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}