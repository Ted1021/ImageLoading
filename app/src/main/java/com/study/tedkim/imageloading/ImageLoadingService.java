package com.study.tedkim.imageloading;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by tedkim on 2017. 9. 16..
 */

public interface ImageLoadingService {

    @GET("/Images/Thumbnails/{filePath}/{fileName}")
    Call<ResponseBody> imageList(@Path("filePath") String filePath, @Path("fileName") String fileName);
}
