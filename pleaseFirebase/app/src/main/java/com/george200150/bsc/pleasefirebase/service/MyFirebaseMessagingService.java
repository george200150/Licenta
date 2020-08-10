package com.george200150.bsc.pleasefirebase.service;

import android.util.Log;

import com.george200150.bsc.pleasefirebase.MainActivity;
import com.george200150.bsc.pleasefirebase.util.ApiUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    /*private APIService mAPIService;*/

    private String PLANT = null;
    private String TOPIC = null;

    public MyFirebaseMessagingService(){
        /*mAPIService = ApiUtils.getAPIService();*/
    }

    // TODO: DOES NOT WORK WHEN APP IS CLOSED OR IN BACKGROUND !!! (WHEN YOU CLICK ON NOTIFICATION ICON)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            TOPIC = remoteMessage.getData().get("TOPIC");
            PLANT = remoteMessage.getData().get("PLANT");

            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
            Log.d(TAG, "unsubscribeFromTopic: " + TOPIC);

            Log.d(TAG, "onMessageReceived: PLANT YES DADDY'O: " + PLANT);
            // TODO: access from origin thread the MainActivity static textField and write that info there (for the moment)
            // TODO: how send information from services to GUI in android
            // https://www.linkedin.com/pulse/android-how-send-data-from-service-activity-mahesh-gawale/
            // https://stackoverflow.com/questions/37322312/send-string-from-service-to-activity
            // https://stackoverflow.com/questions/12997463/send-intent-from-service-to-activity

            getMainExecutor().execute(new Runnable() {
                @Override public void run() {
                    // Code will run on the main thread
                    // MainActivity.mResponseTv2.setText(PLANT); TODO: does not work...
                    MainActivity.doToast(PLANT);
                }
            });

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
