package com.kocemre.artbookv2.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.kocemre.artbookv2.R;
import com.kocemre.artbookv2.model.Art;
import com.kocemre.artbookv2.roomdb.ArtDao;
import com.kocemre.artbookv2.roomdb.ArtDatabase;

import java.io.ByteArrayOutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SelectionFragment extends Fragment {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    ArtDatabase db;
    ArtDao artDao;

    EditText artNameText;
    EditText artistNameText;
    EditText notesText;
    ShapeableImageView selectImageView;

    String artName;
    String artistName;
    String notes;
    byte[] bArray;

    Bitmap image;

    Art art;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    ByteArrayOutputStream bos;


    public SelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //db = Room.databaseBuilder(getActivity().getApplicationContext(), ArtDatabase.class, "Art").allowMainThreadQueries().build();
        db = Room.databaseBuilder(getActivity().getApplicationContext(), ArtDatabase.class, "Art").build();
        artDao = db.artDao();
        registerLauncher();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artNameText = view.findViewById(R.id.artNameEditText);
        artistNameText = view.findViewById(R.id.artistNameEditText);
        notesText = view.findViewById(R.id.notesEditText);
        selectImageView = view.findViewById(R.id.selectImageView);


        Button button = view.findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(view);
            }
        });

        selectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });


    }

    public void save(View view) {

        artName = artNameText.getText().toString();
        artistName = artistNameText.getText().toString();
        notes = notesText.getText().toString();


        if (artName.compareToIgnoreCase("") != 0
                && artistName.compareToIgnoreCase("") != 0
                && notes.compareToIgnoreCase("") != 0
                && image != null) {


            //Scaling image for DB
            Bitmap scaledBitmap = scaledBitmap(image, 300);


            //Converting image to byte array
            bos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bArray = bos.toByteArray();


            art = new Art(artName, artistName, notes, bArray);

            compositeDisposable.add(artDao.insert(art).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe());


            //Go to main fragment after clicking save button
            Navigation.findNavController(view).navigate(SelectionFragmentDirections.actionSelectionFragmentToMainFragment());


        } else {
            Toast.makeText(getParentFragment().getContext(), "Please fill required fields!", Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap scaledBitmap(@NonNull Bitmap bitmap, int maximumSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void selectImage(View view) {

        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != (PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Snackbar.make(view, "Permission needed for selecting image.", Snackbar.LENGTH_INDEFINITE).setAction("Please give permission.", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();

            } else {

                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        } else {

            //Go to gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }

    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Uri uri = intent.getData();

                        try {
                            if (Build.VERSION.SDK_INT >= 28) {

                                ImageDecoder.Source source = ImageDecoder.createSource(SelectionFragment.this.getContext().getContentResolver(), uri);
                                image = ImageDecoder.decodeBitmap(source);
                                selectImageView.setImageBitmap(image);

                            } else {

                                image = MediaStore.Images.Media.getBitmap(SelectionFragment.this.getContext().getContentResolver(), uri);
                                selectImageView.setImageBitmap(image);

                            }


                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {

                if (result) {
                    //Go to Gallery
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    Toast.makeText(SelectionFragment.this.getContext(), "Permission denied. Please give permission to use the app effectively.", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}