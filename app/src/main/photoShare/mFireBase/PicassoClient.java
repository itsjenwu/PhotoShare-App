package com.example.chien.photoshare.mFireBase;

import android.content.Context;
import android.widget.ImageView;

import com.example.chien.photoshare.R;
import com.squareup.picasso.Picasso;

/**
 * Created by chien on 4/30/2017.
 */

public class PicassoClient {


    public static void downloadImage(Context c, String url, ImageView img){

        if(url != null && url.length()>0)
        {
            Picasso.with(c).load(R.drawable.placeholder).into(img);
        }
    }
}
