package com.study.tedkim.imageloading;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by tedkim on 2017. 9. 16..
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    Context mContext;
    ArrayList<String> mDataset = new ArrayList<>();
    LayoutInflater mLayoutInflater;

    public ImageAdapter(Context context, ArrayList<String> dataset) {

        mContext = context;
        mDataset = dataset;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);

            mThumbnail = (ImageView) itemView.findViewById(R.id.imageView_thumbnail);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mLayoutInflater.inflate(R.layout.image_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Bitmap thumbnail = ImageCache.getInstance().getBitmapFromCache(mDataset.get(position));

        if (thumbnail != null) {
            holder.mThumbnail.setImageBitmap(thumbnail);
        } else {
            holder.mThumbnail.setImageBitmap(null);
            DownloadImageAsync downloadImage = new DownloadImageAsync(this, 172, 172);
            downloadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDataset.get(position));
        }
    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}
