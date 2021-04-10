package com.george200150.bsc.pleasefirebase.service;

import android.util.Log;

import com.george200150.bsc.pleasefirebase.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    /*private APIService mAPIService;*/

    private String PIXEL = null;
    private String SIZE = null;
    private String COUNT = null;
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
            PIXEL = remoteMessage.getData().get("PIXEL");
            SIZE = remoteMessage.getData().get("SIZE");
            COUNT = remoteMessage.getData().get("COUNT");

            if ("".equals(PIXEL)) { // TODO: if last notification is empty, finish receiving notifs.
                FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Code will run on the main thread
                        MainActivity.f();
                    }
                });
            } else {
//            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
                Log.d(TAG, "unsubscribeFromTopic: " + TOPIC);

                Log.d(TAG, "onMessageReceived: PIXEL YES DADDY'O: " + PIXEL);
                // TODO: access from origin thread the MainActivity static textField and write that info there (for the moment)
                // TODO: how send information from services to GUI in android
                // https://www.linkedin.com/pulse/android-how-send-data-from-service-activity-mahesh-gawale/
                // https://stackoverflow.com/questions/37322312/send-string-from-service-to-activity
                // https://stackoverflow.com/questions/12997463/send-intent-from-service-to-activity

                getMainExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Code will run on the main thread
                        MainActivity.doToast(PIXEL, COUNT, SIZE);
                    }
                });
            }

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                String message = remoteMessage.getNotification().getBody();
                if (message != null && !message.contains("_TOKEN_")) {
                    Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                }
            }
        }
    }
}
