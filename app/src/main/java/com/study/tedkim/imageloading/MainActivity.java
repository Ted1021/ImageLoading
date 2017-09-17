package com.study.tedkim.imageloading;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String GALLERY_URL = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";
    private static final String BASE_URL = "http://www.gettyimagesgallery.com";

    private ArrayList<ThumbnailData> mImgUrls = new ArrayList<>();
    private ArrayList<Bitmap> mDataset = new ArrayList<>();

    Button mDownloadImage, mSetImage;
    RecyclerView mImageList;
    ImageAdapter mAdapter;

    ImageCache mImageCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initImageCache();
        initView();
        setRecyclerView();
    }

    private void initImageCache(){

        mImageCache = ImageCache.getInstance();
        mImageCache.initImageCache();
    }

    private void initView() {

        mDownloadImage = (Button) findViewById(R.id.button_download);
        mDownloadImage.setOnClickListener(this);

        mImageList = (RecyclerView) findViewById(R.id.recyclerView_imageList);
    }

    private void setRecyclerView() {

        mAdapter = new ImageAdapter(MainActivity.this, mDataset);
        mImageList.setAdapter(mAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 2);
        mImageList.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.button_download:
                getImageRes();
                break;
        }
    }

    // 주어진 html 파일로부터 thumbnail 접근 경로를 받아오는 로직
    private void getImageRes() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {

                StringBuffer stringBuffer = new StringBuffer();
                try {
                    Document document = Jsoup.connect(GALLERY_URL).get();
                    Elements imgs = document.select(".picture");

                    for (Element img : imgs) {
                        String[] components = img.attr("src").split("/");

                        ThumbnailData data = new ThumbnailData();
                        data.setFilePath(components[3]);
                        data.setFileName(components[4]);
                        mImgUrls.add(data);
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

                setData();
            }
        }.execute();
    }

    private void setData() {

        for (ThumbnailData data : mImgUrls) {
            downloadImages(data);
        }
        Toast.makeText(MainActivity.this, "Image Download complete!", Toast.LENGTH_SHORT).show();
    }

    // 받아온 thumbnail 경로를 바탕으로 bitmap arraylist 에 저장
    private void downloadImages(ThumbnailData data) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        ImageLoadingService service = retrofit.create(ImageLoadingService.class);

        Call<ResponseBody> call = service.imageList(data.getFilePath(), data.getFileName());
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        mDataset.add(bitmap);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("FAIL_DOWNLOAD", "fail to download thumbnail from server");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e("FAIL_DOWNLOAD", "fail to connect");
            }
        });
    }
}