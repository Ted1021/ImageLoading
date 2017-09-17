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
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String VIEWER_URL = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";
    private static final String BASE_URL = "http://www.gettyimagesgallery.com";

    private ArrayList<String> mImgUrls = new ArrayList<>();
    private ArrayList<Bitmap> mDataset = new ArrayList<>();
    private HashMap<String, String> mImageFileSet = new HashMap<>();

    Button mDownloadImage, mSetImage;
    RecyclerView mImageList;
    ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setRecyclerView();
    }

    private void initView(){
        mDownloadImage = (Button) findViewById(R.id.button_download);
        mDownloadImage.setOnClickListener(this);

        mSetImage = (Button) findViewById(R.id.button_setImages);
        mSetImage.setOnClickListener(this);

        mImageList = (RecyclerView) findViewById(R.id.recyclerView_imageList);
    }

    private void setRecyclerView(){

        mAdapter = new ImageAdapter(MainActivity.this, mDataset);
        mImageList.setAdapter(mAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mImageList.setLayoutManager(layoutManager);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.button_download:
                getImageRes();
                break;

            case R.id.button_setImages:
                break;
        }
    }

    private void getImageRes(){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Document document = Jsoup.connect(VIEWER_URL).get();
                    Elements imgs = document.select(".picture");

                    for(Element img : imgs){
                        String[] components = img.attr("src").split("/");
                        mImageFileSet.put(components[2], components[3]);
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

                Toast.makeText(MainActivity.this, "Download complete", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void setData(){



    }

    private void downloadImages(String no, String fileName){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        ImageLoadingService service = retrofit.create(ImageLoadingService.class);
        Call<ResponseBody> call = service.imageList(no, fileName);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    if(response.body() != null){
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        mDataset.add(bitmap);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        mAdapter.notifyDataSetChanged();
    }
}