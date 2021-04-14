package com.george200150.bsc.pleasefirebase.util;

import com.george200150.bsc.pleasefirebase.service.APIService;

public class ApiUtils {

    private ApiUtils() {}

    // MUST USE android:usesCleartextTraffic="true" TO ACCESS http (not secured)
//    public static final String BASE_URL = "http://192.168.1.2:8080/"; // server IP
    public static final String BASE_URL = "http://www.depthonfield.ml:8080/"; // server HTTP IP

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}