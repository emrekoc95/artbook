package com.kocemre.artbookv2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.kocemre.artbookv2.databinding.RecyclerviewRowBinding;
import com.kocemre.artbookv2.fragments.MainFragment;
import com.kocemre.artbookv2.fragments.MainFragmentDirections;
import com.kocemre.artbookv2.model.Art;
import com.kocemre.artbookv2.roomdb.ArtDao;
import com.kocemre.artbookv2.roomdb.ArtDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ArtHolder> {

    List<Art> artList = new ArrayList<>();
    MainFragment mainFragment = new MainFragment();
    private Context context;

    public RecyclerViewAdapter(MainFragment mainFragment, List<Art> artList) {
        this.artList = artList;
        this.mainFragment = mainFragment;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerviewRowBinding recyclerviewRowBinding = RecyclerviewRowBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ArtHolder(recyclerviewRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {

        byte[] bArray = artList.get(position).getByteArray();
        String artName = artList.get(position).getArtName();
        String artistName = artList.get(position).getArtistName();
        String notes = artList.get(position).getNotes();
        int id = artList.get(position).getId();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bArray, 0, bArray.length);


        holder.recyclerviewRowBinding.selectImageView.setImageBitmap(bitmap);
        holder.recyclerviewRowBinding.recyclerViewTextView.setText(artList.get(position).getArtName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragmentDirections.ActionMainFragmentToArtFragment action = MainFragmentDirections.actionMainFragmentToArtFragment(id);
                Navigation.findNavController(view).navigate(action);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == DialogInterface.BUTTON_POSITIVE) {

                            Art art = artList.get(position);

                            artList.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();

                            CompositeDisposable compositeDisposable = new CompositeDisposable();

                            ArtDatabase db = Room.databaseBuilder(context, ArtDatabase.class, "Art").build();
                            ArtDao artDao = db.artDao();


                            compositeDisposable.add(artDao.delete(art).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());

                            //artDao.delete(art);
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure to delete an item?").setPositiveButton("Yes", listener)
                        .setNegativeButton("No", listener).show();
                return true;
            }


        });
    }


    @Override
    public int getItemCount() {
        return artList.size();
    }


    public class ArtHolder extends RecyclerView.ViewHolder {
        RecyclerviewRowBinding recyclerviewRowBinding;

        public ArtHolder(RecyclerviewRowBinding recyclerviewRowBinding) {
            super(recyclerviewRowBinding.getRoot());
            this.recyclerviewRowBinding = recyclerviewRowBinding;

        }
    }
}
