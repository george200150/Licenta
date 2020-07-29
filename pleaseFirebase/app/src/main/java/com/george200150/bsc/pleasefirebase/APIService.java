package com.george200150.bsc.pleasefirebase;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    @POST("/data/bitmap")
    Call<Plant> sendBitmapPOST(@Body Bitmap bitmap);

    @GET("/data/records/LAT/{name}")
    Call<Plant> sendLatinGET(@Path("name") String name);
}