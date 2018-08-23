package com.example.chien.photoshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.chien.photoshare.mFireBase.FireBaseClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity will imemeidately pop up after login activity
 * TODO: grab data from firebase and implement comment into arraylist
 */

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    private FirebaseDatabase mDatabase;
    private DatabaseReference newRef;
    public String currentPhotoUserId;

    ListView listViewPhotos;
    List<Photo> photoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance();
        newRef = mDatabase.getReference("Photos");

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.camera){

                    //get currentUser
                    currentPhotoUserId = getIntent().getStringExtra("userID");
                    Intent intent = new Intent(getBaseContext(), UploadActivity.class);
                    intent.putExtra("userID" , currentPhotoUserId);
                    intent.putExtra("buttonClicked", R.id.camera);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.menu_home){
                        return true;
                }
                else if(item.getItemId() == R.id.account){

                    currentPhotoUserId = getIntent().getStringExtra("userID");
                    Intent intent = new Intent(getBaseContext(), AccountActivity.class);
                    intent.putExtra("userID", currentPhotoUserId);
                    startActivity(intent);
                }
                else{
                    return false;
                }

                return true;
            }
        });

        //retrieve info


        listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
        photoList = new ArrayList<>();



        newRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                photoList.clear();
                for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                    Photo photo = new Photo();
                    String url = photoSnapshot.child("photoUrl").getValue().toString();
                    String userId = photoSnapshot.child("userId").getValue().toString();
                    photo.setPhotoUrl(url);
                    photo.setUserId(userId);

                    photoList.add(photo);
                }

                PhotoList adapter = new PhotoList(MainActivity.this, photoList);
                listViewPhotos.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("PhotoShare", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


}

