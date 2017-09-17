package com.study.tedkim.imageloading;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String GALLERY_URL = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";
    private static final String BASE_URL = "http://www.gettyimagesgallery.com";

    private ArrayList<String> mImgUrls;
    private ArrayList<String> mPageDataset = new ArrayList<>();

    RecyclerView mImageList;
    ImageAdapter mAdapter;

    ImageCache mImageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageCache();
        initView();
        getImageRes();
        setRecyclerView();
    }

    private void initImageCache() {

        mImageCache = ImageCache.getInstance();
        mImageCache.initImageCache();
    }

    private void initView() {

        mImageList = (RecyclerView) findViewById(R.id.recyclerView_imageList);
    }

    private void setRecyclerView() {

        mAdapter = new ImageAdapter(MainActivity.this, mPageDataset);
        mImageList.setAdapter(mAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        mImageList.setLayoutManager(layoutManager);
        mImageList.addOnScrollListener(new EndlessGridRecyclerViewScrollListner(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                for(int i=10*page; i<10*(page+1); i++){
                    mPageDataset.add(mImgUrls.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    // 주어진 html 파일로부터 thumbnail 접근 경로를 받아오는 로직
    private void getImageRes() {

        mImgUrls = new ArrayList<>();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Document document = Jsoup.connect(GALLERY_URL).get();
                    Elements imgs = document.select(".picture");

                    for (Element img : imgs) {
                        mImgUrls.add(BASE_URL + img.attr("src"));
                        Log.d("CHECK_URL", BASE_URL + img.attr("src"));
                    }

                } catch (IOException e) {
                    Log.e("FAIL_GET_IMAGE_RES", "Fail to get image urls");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                for(int i=0; i<10; i++) {
                    mPageDataset.add(mImgUrls.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
}