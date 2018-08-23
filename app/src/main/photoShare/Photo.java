package com.example.chien.photoshare;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * Created by jen on 4/15/2017.
 */

public class Photo implements Parcelable {

    public String photoUrl;
    public String userId;
    public int likes;
    public ArrayList<String> comments = new ArrayList<String>();
    public String comment;

    public String getPhotoUrl(){
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String ID){this.userId = ID;}

    public int getLikes(){
        return likes;
    }
    public void addLike(){
        int currentLikes = getLikes();
        currentLikes = currentLikes + 1;
        this.likes = currentLikes;

    }

    public void setLikes(int likes) {this.likes = likes;}

    public ArrayList<String> getComments(){
        return comments;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public ArrayList<String> addComment(String comment){
        ArrayList<String> updateComment = this.comments;
        updateComment.add(comment);
        return updateComment;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){

        dest.writeString(this.photoUrl);
        dest.writeString(this.userId);
    }

    //to add url to display all images
    public Photo(){}

    //skip log in page
    public Photo(String userID){

        this.userId = userID;

    }

    //to push object to database
    public Photo(String userId, String photoUrl, int likes, ArrayList<String> comments){

        this.comments = comments;
        this.likes = likes;
        this.photoUrl = photoUrl;
        this.userId = userId;
    }

    protected Photo(Parcel in){
        this.photoUrl = in.readString();

    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>(){
        @Override
        public Photo createFromParcel(Parcel source){
            return new Photo(source);
        }
        @Override
        public Photo[] newArray(int size){
            return new Photo[size];
        }
    };
}
