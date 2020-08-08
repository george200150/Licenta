package com.george200150.bsc.pleasefirebase.service;

import android.util.Log;
import android.view.View;

import com.george200150.bsc.pleasefirebase.util.ApiUtils;
import com.george200150.bsc.pleasefirebase.MainActivity;
import com.george200150.bsc.pleasefirebase.model.Plant;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private APIService mAPIService;

    private String TOPIC = null;

    public MyFirebaseMessagingService(){
        mAPIService = ApiUtils.getAPIService();
    }

//    @Override
//    public void onNewToken(String s) {
//        super.onNewToken(s);
//        Log.e("newToken", s);
//        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply(); // no idea what this does..
//    }


    // TODO: DOES NOT WORK WHEN APP IS CLOSED OR IN BACKGROUND !!! (WHEN YOU CLICK ON NOTIFICATION ICON)

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            TOPIC = remoteMessage.getData().get("TOPIC");
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            if (message != null && !message.contains("_TOKEN_")){
                String latinName = message;
                this.sendLatinName(latinName);
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        }
    }

    public void sendLatinName(String latinName) {

        mAPIService.sendLatinGET(latinName).enqueue(new Callback<Plant>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {

                if(response.isSuccessful()) {

                    // unsubscribe from topic (cannot be null)
                    // TODO: removed for testing FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                    TOPIC = null;

                    Log.d(TAG, "THIS IS THE BIG RESPONSE, DADDY-O'" + response.body().toString());

                    Log.i(TAG, "post submitted to API." + response.body().toString());

                    if(MainActivity.mResponseTv2.getVisibility() == View.GONE) {
                        MainActivity.mResponseTv2.setVisibility(View.VISIBLE);
                    }
                    MainActivity.mResponseTv2.setText(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {

                Log.d(TAG, t.toString());
                System.out.println("showErrorMessage();");

                // TODO: should retry... (or, at least notify user and unsubscribe)

                Log.e(TAG, "Unable to submit post to API: {}", t);
            }
        });
    }
}
