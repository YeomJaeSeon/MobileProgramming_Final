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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TimeService extends Service {

    Notification notification;
    NotificationChannel notificationChannel;
    NotificationManager notificationManager;
    Notification.Builder builder;
    public double wholeTime;

    private DatabaseReference mDatabase;
    DateAndTimer dateAndTimer;

    class TimeBinder extends Binder {
        TimeService getService() {
            return TimeService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {

        super.onCreate();
        dateAndTimer = new DateAndTimer();
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel("timeService", "timeService", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(notificationChannel);
        builder = new Notification.Builder(this, notificationChannel.getId());


        //first build
        builder.setContentTitle("공부방울")
                .setContentText(dateAndTimer.getTimerText(((MainActivity) MainActivity.mContext).sum))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_main_foreground);
        notificationManager.notify(1234, builder.build());
        //database 가져오기
        //Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("times").child(dateAndTimer.curTimeArr[1]).child(dateAndTimer.curTimeArr[2]).child("wholeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //second build
                Log.d("VALVAL",snapshot.getValue().toString());
                double value=Double.parseDouble(snapshot.getValue().toString());
                builder.setContentTitle(dateAndTimer.curTimeArr[1]+"월 "+dateAndTimer.curTimeArr[2]+"일 공부시간").setContentText(dateAndTimer.getTimerText(value));
                notificationManager.notify(1234, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
