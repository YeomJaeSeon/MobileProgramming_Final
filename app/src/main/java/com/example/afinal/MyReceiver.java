package com.example.afinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public static final String MY_ACTION = "com.example.afinal.action.ACTION_MY_BROADCAST";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MY_ACTION.equals(intent.getAction())){

        }
    }
}
