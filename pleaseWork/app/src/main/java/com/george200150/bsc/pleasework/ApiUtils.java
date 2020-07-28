package com.george200150.bsc.pleasework;

public class ApiUtils {

    private ApiUtils() {
    }

    // MUST USE android:usesCleartextTraffic="true" TO ACCESS http (not secured)

    // Make sure you end the base URL with a /
//    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
//    public static final String BASE_URL = "http://jsonplaceholder.typicode.com/";

//    public static final String BASE_URL = "http://192.168.1.45:8080/data/";
//    public static final String BASE_URL = "http://dummy.restapiexample.com/api/v1/";

    //public static final String BASE_URL = "http://10.0.2.2/data/";
    public static final String BASE_URL = "http://192.168.1.45/";


    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}