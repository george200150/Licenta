package com.george200150.bsc.pleasefirebase.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean notif = intent.getBooleanExtra("state", false);
        if (notif){
            Toast.makeText(context, "YEEEEES", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "NOOOOO?", Toast.LENGTH_LONG).show();
        }
    }
}
