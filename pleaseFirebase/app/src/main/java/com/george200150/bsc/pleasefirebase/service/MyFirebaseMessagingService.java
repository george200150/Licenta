package com.george200150.bsc.pleasefirebase.service;

import android.content.Intent;
import android.util.Log;

import com.george200150.bsc.pleasefirebase.model.SubscriptionMessages;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String TOPIC = remoteMessage.getData().get("TOPIC");
            String RESOURCE = remoteMessage.getData().get("RESOURCE");

            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);
            Intent intent = new Intent(SubscriptionMessages.NOTIFICATION_ARRIVED);
            intent.putExtra("RESOURCE", RESOURCE);

            Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification());
            Log.d(TAG, "onMessageReceived: intent = " + intent.toString());
            Log.d(TAG, "onMessageReceived: intent = " + intent.getExtras().toString());

            sendBroadcast(intent);
            getBaseContext().sendBroadcast(intent);
            getApplicationContext().sendBroadcast(intent);
        }
    }
}
