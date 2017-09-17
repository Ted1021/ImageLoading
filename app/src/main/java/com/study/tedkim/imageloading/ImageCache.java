package com.study.tedkim.imageloading;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by tedkim on 2017. 9. 17..
 */

public class ImageCache {

    private LruCache<String, Bitmap> mImageSet;
    private static ImageCache mInstance;

    public static ImageCache getInstance() {

        if (mInstance == null) {
            mInstance = new ImageCache();
        }
        return mInstance;
    }

    public void initImageCache() {

        final int maxMemSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemSize / 8;

        mImageSet = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {

                int bitmapByteCount = value.getRowBytes() * value.getHeight();
                return bitmapByteCount / 1024;
            }
        };
    }

    public void addBitmapToCache(String key, Bitmap value) {
        if (mImageSet != null && mImageSet.get(key) == null) {
            mImageSet.put(key, value);
        }
    }

    public Bitmap getBitmapFromCache(String key) {

        if (key != null) {
            return mImageSet.get(key);
        } else {
            return null;
        }
    }

    public void removeBitmapFromCache(String key) {
        mImageSet.remove(key);
    }

    public void clearCache() {

        if (mImageSet != null) {
            mImageSet.evictAll();
        }
    }
}
