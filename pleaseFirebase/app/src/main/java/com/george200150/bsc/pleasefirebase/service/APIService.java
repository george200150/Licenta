package com.george200150.bsc.pleasefirebase.service;

import com.george200150.bsc.pleasefirebase.model.Bitmap;
import com.george200150.bsc.pleasefirebase.model.ForwardMessage;
import com.george200150.bsc.pleasefirebase.model.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("/data/bitmap")
    Call<Token> sendBitmapPOST(@Body ForwardMessage forwardMessage);

    @POST("/data/fetch/")
    Call<Bitmap> sendFetchPOST(@Body String pathname);
}