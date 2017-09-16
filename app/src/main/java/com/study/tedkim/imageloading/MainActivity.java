package com.study.tedkim.imageloading;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String VIEWER_URL = "http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";
    private ArrayList<String> mImgUrls = new ArrayList<>();

    Button mDownloadImage, mSetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView(){
        mDownloadImage = (Button) findViewById(R.id.button_download);
        mDownloadImage.setOnClickListener(this);

        mSetImage = (Button) findViewById(R.id.button_setImages);
        mSetImage.setOnClickListener(this);
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
                        mImgUrls.add(img.attr("src"));
                        Log.d("CHECK_IMG_URL", img.attr("src"));
                    }

                } catch (IOException e) {
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
}
