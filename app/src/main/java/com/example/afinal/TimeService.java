package com.example.afinal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


public class TimeService extends Service {

    Notification notification;
    NotificationChannel notificationChannel;
    NotificationManager notificationManager;

    class TimeBinder extends Binder {
        TimeService getService() {
            return TimeService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {

        super.onCreate();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntentintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel("timeService", "timeService", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification.Builder builder = new Notification.Builder(this, notificationChannel.getId());


            notification = builder.setContentTitle("공부방울")
                    .setContentText("시간 적자")
                    .setContentIntent(pendingIntentintent)
                    .setSmallIcon(R.mipmap.ic_main_foreground)
                    .build();
        startForeground(123, notification);
    }

    TimeBinder mBinder = new TimeBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        notificationManager.cancelAll();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

}
