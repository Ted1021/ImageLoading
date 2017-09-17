package com.study.tedkim.imageloading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tedkim on 2017. 9. 17..
 */

public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {

    private int inSampleSize = 0;
    private int desiredWidth, desiredHeight;

    private RecyclerView.Adapter mAdapter;
    private ImageCache mCache;
    private String mImageUrl;
    private Bitmap mImage = null;

    public DownloadImageAsync(RecyclerView.Adapter adapter, int desiredWidth, int desiredHeight)
    {
        mAdapter = adapter;
        this.mCache = ImageCache.getInstance();
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;
    }

    public DownloadImageAsync(ImageCache cache, int desiredWidth, int desiredHeight) {

        this.mCache = cache;
        this.desiredWidth = desiredWidth;
        this.desiredHeight = desiredHeight;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        mImageUrl = params[0];
        return getImage(mImageUrl);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        if (result != null) {
            mCache.addBitmapToCache(mImageUrl, result);
        }
    }

    // 큰 이미지에 대해 현재 화면에 알맞게 이미지를 처리하는 로직 추가
    private Bitmap getImage(String imageUrl) {

        if (mCache.getBitmapFromCache(imageUrl) == null) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = inSampleSize;

            try {

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream stream = connection.getInputStream();
                mImage = BitmapFactory.decodeStream(stream, null, options);

                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;

                if (imageWidth > desiredWidth || imageHeight > desiredHeight) {

                    inSampleSize = inSampleSize + 2;
                    getImage(imageUrl);

                } else {

                    options.inJustDecodeBounds = false;

                    connection = (HttpURLConnection) url.openConnection();
                    stream = connection.getInputStream();
                    mImage = BitmapFactory.decodeStream(stream, null, options);

                    return mImage;
                }
            } catch (Exception e) {
                Log.e("CHECK_IMAGE", e.toString());
            }
        }

        return mImage;
    }
}
