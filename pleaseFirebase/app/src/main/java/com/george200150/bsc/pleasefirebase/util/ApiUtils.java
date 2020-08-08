package com.george200150.bsc.pleasefirebase.util;

import com.george200150.bsc.pleasefirebase.service.APIService;

public class ApiUtils {

    private ApiUtils() {
    }

    // MUST USE android:usesCleartextTraffic="true" TO ACCESS http (not secured)
    // Make sure you end the base URL with a /
    public static final String BASE_URL = "http://192.168.1.45:8080/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}