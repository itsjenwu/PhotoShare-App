package com.example.chien.photoshare.mFireBase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.chien.photoshare.Photo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by chien on 4/30/2017.
 */

public class FireBaseClient {

    Context c;
    String DB_URL;
    RecyclerView rv;

    FirebaseDatabase mDatabase;
    private DatabaseReference newRef;

   // Firebase fire;
    ArrayList<Photo> photos = new ArrayList<>();
    PhotoAdapter adapter;

    public FireBaseClient(Context c, String DB_URL, RecyclerView rv) {
        this.c = c;
        this.DB_URL = DB_URL;
        this.rv = rv;

        //retrieve info
        mDatabase = FirebaseDatabase.getInstance();
        newRef = mDatabase.getReference("Photos");

    }

    /*
    //SAVE
    public void saveOnline(String name, String url) {

        Photo p = new Photo();
        p.setPhotoUrl(url);

        fire.child("Photos").push().setValue(p);
    }

    */

    //RETRIEVE
    public void refreshData() {

      newRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
          @Override
          public void onChildAdded(DataSnapshot dataSnapshot, String s) {

              getUpdates(dataSnapshot);
          }

          @Override
          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

              getUpdates(dataSnapshot);
          }

          @Override
          public void onChildRemoved(DataSnapshot dataSnapshot) {

          }

          @Override
          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
    }

    private void getUpdates(DataSnapshot dataSnapshot) {

        photos.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Photo p = new Photo();
            p.setPhotoUrl(ds.getValue(Photo.class).getPhotoUrl());

            photos.add(p);
        }

        if (photos.size() > 0) {
            adapter = new PhotoAdapter(c, photos);
            rv.setAdapter(adapter);
        }
        else{
            Toast.makeText(c,"No data", Toast.LENGTH_SHORT).show();
        }
    }
}

