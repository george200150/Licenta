package com.george200150.bsc.pleasefirebase.service;

import com.george200150.bsc.pleasefirebase.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private String RESOURCE = null;
    private String TOPIC = null;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            TOPIC = remoteMessage.getData().get("TOPIC");
            RESOURCE = remoteMessage.getData().get("RESOURCE");

            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);

            getMainExecutor().execute(() -> {
                // Code will run on the main thread
                MainActivity.doToast(RESOURCE);
            });

            // TODO: in loc de static, trebuie sa faca un broadcast service-ul la signal-ul la care este abonat MainActivity
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getBody();
//            if (message != null && !message.contains("_TOKEN_")) {
//                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            }
        }
    }
}
