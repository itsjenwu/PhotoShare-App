package com.example.chien.photoshare.mFireBase;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.chien.photoshare.Photo;
import com.example.chien.photoshare.R;

import java.util.ArrayList;

/**
 * Created by chien on 4/30/2017.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{


    public static final String PHOTO = "PHOTO";
    Context c;
    ArrayList<Photo> photos;

    public PhotoAdapter(Context c, ArrayList<Photo> photos) {

        this.c = c;
        this.photos = photos;
    }

    /**
     * @param parent
     * @param viewType
     * @return new ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View photoListItem = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.photo_list_items, parent, false);
        return new ViewHolder(photoListItem);
    }

    /**
     * gets data from MovieCollection and puts into ViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Photo photo = photos.get(position);

        PicassoClient.downloadImage(c, photos.get(position).getPhotoUrl(),holder.imageView);
    }

    /**
     * @return size of arraylist "movies"
     */
    @Override
    public int getItemCount() {
        return photos.size();
    }

    /**
     * ViewHolder class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.photoView);
        }
    }

}
