package com.george200150.bsc.pleasefirebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private APIService mAPIService;

    public MyFirebaseMessagingService(){
        mAPIService = ApiUtils.getAPIService();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();
    }


    // TODO: DOES NOT WORK WHEN APP IS CLOSED !!! (WHEN YOU CLICK ON NOTIFICATION ICON)

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());



// TODO: nici macar nu intra aici
//        if (remoteMessage.getData().size() > 0) {
//            //sendLatinName();
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                System.out.println("scheduleJob();");
//            } else {
//                // Handle message within 10 seconds
//                System.out.println("handleNow();");
//            }
//        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String latinName = remoteMessage.getNotification().getBody();
            this.sendLatinName(latinName);
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    public void sendLatinName(String latinName) {

        mAPIService.sendLatinGET("Acer pseudoplatanus").enqueue(new Callback<Plant>() {
            @Override
            public void onResponse(Call<Plant> call, Response<Plant> response) {

                if(response.isSuccessful()) {


                    Log.d(TAG, response.body().toString());
                    System.out.println("showResponse(response.body().toString());");


                    Log.i(TAG, "post submitted to API." + response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<Plant> call, Throwable t) {


                Log.d(TAG, t.toString());
                System.out.println("showErrorMessage();");


                Log.e(TAG, "Unable to submit post to API: {}", t);
            }
        });
    }
}
