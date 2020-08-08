package com.george200150.bsc.pleasefirebase.service;

import com.george200150.bsc.pleasefirebase.model.ForwardMessage;
import com.george200150.bsc.pleasefirebase.model.Plant;
import com.george200150.bsc.pleasefirebase.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {

    @POST("/data/bitmap")
    Call<Token> sendBitmapPOST(@Body ForwardMessage forwardMessage);

    @GET("/data/records/LAT/{name}")
    Call<Plant> sendLatinGET(@Path("name") String name);
}