package com.example.chien.photoshare;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chien on 4/18/2017.
 */

public class PhotoList extends ArrayAdapter<Photo> {

    private FirebaseDatabase mDatabase;
    private DatabaseReference newRef;
    private Activity context;
    private Photo photo;
    private String photoUrl;
    public List<Photo> photoList;

    public TextView likeCountView;
    public TextView commentView;

    public PhotoList(Activity context, List<Photo> photoList) {

        super(context, R.layout.photo_list_items, photoList);
        this.context = context;
        this.photoList = photoList;

        mDatabase = FirebaseDatabase.getInstance();
        newRef = mDatabase.getReference("Photos");
    }


    public List<Photo> getPhotoList() {
        return photoList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        final View listViewItem = inflater.inflate(R.layout.photo_list_items, null, true);

        ImageView photoView = (ImageView) listViewItem.findViewById(R.id.photoView);
        TextView userIdTextView = (TextView) listViewItem.findViewById(R.id.userIdTextView);
        Button likeButton = (Button) listViewItem.findViewById(R.id.likeButton);
        final Button commentButton = (Button) listViewItem.findViewById(R.id.commentButton);
        likeCountView = (TextView) listViewItem.findViewById(R.id.likeCountTextView);
        commentView = (TextView) listViewItem.findViewById(R.id.commentsTextView);

        //get specific photo
        photo = photoList.get(position);

        likeCountView.setText("Likes: " + photo.getLikes());
        commentView.setText("comments...");
        userIdTextView.setText(photo.getUserId());
        Picasso.with(photoView.getContext()).load(photo.getPhotoUrl()).into(photoView);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo.addLike();
                int updatedLikes = photo.getLikes();
                updateLikes(updatedLikes);
                likeCountView.setText("Likes: " + updatedLikes);

              //  listViewItem.notify();

            }
        });


        commentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder commentDialog = new AlertDialog.Builder(view.getContext());
                commentDialog.setTitle("Comment:");
                commentDialog.setMessage("Enter comment:");

                final EditText input = new EditText(view.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                commentDialog.setView(input);

                commentDialog.setPositiveButton("SAVE",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String comment = input.getText().toString();
                                //add to Photo Object
                                photo.addComment(comment);
                                updateComments(comment);
                                commentView.setText(comment);
                               // listViewItem.notify();

                            }
                        });
                commentDialog.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                commentDialog.show();
            }
        });


        return listViewItem;
    }

    public void updateLikes(final int updatedLikes) {

        mDatabase = FirebaseDatabase.getInstance();
        newRef = mDatabase.getReference("Photos");
        Query queryLike = newRef.orderByChild("photoUrl").equalTo(photo.getPhotoUrl());
        queryLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //updates a new count of total likes
                    snapshot.getRef().child("likes").setValue(updatedLikes);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PHOTOLIST", "Update Likes Failed");
            }
        });
    }

    public void updateComments(final String comment) {

        mDatabase = FirebaseDatabase.getInstance();
        newRef = mDatabase.getReference("Photos");
        Query queryLike = newRef.orderByChild("photoUrl").equalTo(photo.getPhotoUrl());
        queryLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //updates comments
                    ArrayList<String> updateComments = photo.getComments();
                    updateComments.add(comment);

                    String commentNodeId = snapshot.getRef().child("comments").push().getKey();
                    snapshot.getRef().child("comments").child(commentNodeId).setValue(comment);
                    notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("PHOTOLIST", "Update Likes Failed");
            }
        });
        notifyDataSetChanged();
    }


}




