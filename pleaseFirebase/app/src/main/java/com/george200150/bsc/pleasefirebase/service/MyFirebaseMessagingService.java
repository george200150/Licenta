package com.george200150.bsc.pleasefirebase.service;

import android.util.Log;

import com.george200150.bsc.pleasefirebase.util.ApiUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private APIService mAPIService;

    private String TOPIC = null;
    private String PLANT = null;

    public MyFirebaseMessagingService(){
        mAPIService = ApiUtils.getAPIService();
    }

    // TODO: DOES NOT WORK WHEN APP IS CLOSED OR IN BACKGROUND !!! (WHEN YOU CLICK ON NOTIFICATION ICON)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            TOPIC = remoteMessage.getData().get("TOPIC");
            PLANT = remoteMessage.getData().get("PLANT");

            Log.d(TAG, "onMessageReceived: PLANT YES DADDY'O: " + PLANT);
            // TODO: access from origin thread the MainActivity static textField and write that info there (for the moment)
            // TODO: how send information from services to GUI in android
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
            if (message != null && !message.contains("_TOKEN_")){
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            }
        }
    }
}
