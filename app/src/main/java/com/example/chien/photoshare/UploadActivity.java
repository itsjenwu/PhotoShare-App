package com.example.chien.photoshare;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.chien.photoshare.R.id.photoView;

/**
 * Find a way to parse all photos into mainactivity.... and display it....
 * Created by chien on 4/14/2017.
 */

public class UploadActivity extends AppCompatActivity {
    private Button mCameraButton;
    private Button mGalleryButton;
    private BottomNavigationView mBottomNav;
    private Uri photoUri;
    private Uri updatedPhotoUri;
    private FirebaseDatabase mDatabase;
    private String generatedFilePath;
    private ImageView mImageView;

    private static String caption = "";
    private static final int CAMERA_INTENT = 1;
    private static final int GALLERY_INTENT = 2;

    private String currentPhotoUserId;

    private StorageReference mStorage;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        currentPhotoUserId = getIntent().getStringExtra("userID");


        FirebaseApp.initializeApp(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);

        mImageView = (ImageView) findViewById(photoView);
        mCameraButton = (Button) findViewById(R.id.cameraButton);
        mGalleryButton = (Button) findViewById(R.id.galleryButton);

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.menu_home) {

                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.account){

                    currentPhotoUserId = getIntent().getStringExtra("userID");
                    Intent intent = new Intent(getBaseContext(), AccountActivity.class);
                    intent.putExtra("userID", currentPhotoUserId);
                    startActivity(intent);
                }
                else {
                    return false;
                }

                return true;
            }
        });


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mCameraButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mCameraButton.setEnabled(true);
            }
        }
    }


    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoUri = FileProvider.getUriForFile(UploadActivity.this, BuildConfig.APPLICATION_ID + ".provider",

                getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, CAMERA_INTENT);

    }

    public void galleryUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private static File getOutputMediaFile() {

        //check SDCard is mounted using Environment.getExternalStorageState()
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "PhotoShare");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + "_" + caption + ".jpg");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //upload from Camera
        if (requestCode == CAMERA_INTENT && resultCode == RESULT_OK) {
            mImageView.setImageURI(photoUri);

            AlertDialog.Builder captionDialog = new AlertDialog.Builder(UploadActivity.this);
            captionDialog.setTitle("Photo Caption");
            captionDialog.setMessage("Enter caption:");

            final EditText input = new EditText(UploadActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            captionDialog.setView(input);
            captionDialog.setIcon(R.drawable.ic_camera);

            captionDialog.setPositiveButton("SAVE",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            caption = input.getText().toString();

                            updatedPhotoUri = FileProvider.getUriForFile(UploadActivity.this, BuildConfig.APPLICATION_ID +
                                    ".provider", getOutputMediaFile());

                            mProgress.setMessage("Uploading Image...");
                            mProgress.show();

                            StorageReference filepath = mStorage.child("Photos").child(updatedPhotoUri.getLastPathSegment());


                            filepath.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    //retrieves download uri of uploaded pictures
                                    @SuppressWarnings("VisibleForTests")
                                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                                    generatedFilePath = downloadUri.toString();

                                    //adds Photo to database
                                    mDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference newRef = mDatabase.getReference("Photos");
                                    String id = newRef.push().
                                            getKey();
                                    Photo photo = new Photo(currentPhotoUserId, generatedFilePath, 0, null);
                                    newRef.child(id).setValue(photo);

                                    mProgress.dismiss();
                                    Toast.makeText(UploadActivity.this, "Upload Finished!", Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });
            captionDialog.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            captionDialog.show();
        }




        //upload from Gallery
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            mImageView.setImageURI(uri);
            mProgress.setMessage("Uploading Image...");
            mProgress.show();

            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    generatedFilePath = downloadUri.toString();

                    //adds Photo to database
                    mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference newRef = mDatabase.getReference("Photos");
                    String id = newRef.push().getKey();
                    Photo photo = new Photo(currentPhotoUserId, generatedFilePath, 0, null);
                    newRef.child(id).setValue(photo);

                    mProgress.dismiss();
                    Toast.makeText(UploadActivity.this, "Upload Finished!", Toast.LENGTH_LONG).show();
                }
            });


        }
    }

}