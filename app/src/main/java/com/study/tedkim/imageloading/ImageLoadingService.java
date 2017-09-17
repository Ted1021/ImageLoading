package com.study.tedkim.imageloading;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tedkim on 2017. 9. 16..
 */

public interface ImageLoadingService {

    @GET("/Images/Thumbnails/{no}/{fileName}")
    Call<ResponseBody> imageList(@Path("no") String no, @Query("fileName") String fileName);
}
