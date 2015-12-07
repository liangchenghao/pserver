package com.example.chenlian.usbsocket.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.chenlian.usbsocket.MainActivity;

/**
 * Created by chenlian on 11/30/2015.
 */
public class ServiceBroadcastReceiver extends BroadcastReceiver {
    private static String START_ACTION = "NotifyServiceStart";
    private static String STOP_ACTION = "NotifyServiceStop";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(AndroidService.TAG, Thread.currentThread().getName() + "---->"
                + "ServiceBroadcastReceiver onReceive");

        String action = intent.getAction();
        if (START_ACTION.equalsIgnoreCase(action)) {
            context.startService(new Intent(context, AndroidService.class));

            Log.d(AndroidService.TAG, Thread.currentThread().getName() + "---->"
                    + "ServiceBroadcastReceiver onReceive start end");
        } else if (STOP_ACTION.equalsIgnoreCase(action)) {
            context.stopService(new Intent(context, AndroidService.class));
            Log.d(AndroidService.TAG, Thread.currentThread().getName() + "---->"
                    + "ServiceBroadcastReceiver onReceive stop end");
        }
    }

}