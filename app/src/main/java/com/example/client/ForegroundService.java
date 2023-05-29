package com.example.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public ForegroundService() {
    }

    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
//        String input = intent.getStringExtra("InputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("SMART")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);



        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::biometricCheck, 0, 60, TimeUnit.SECONDS);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Biometric Check",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
             NotificationManager manager = getSystemService(NotificationManager.class);
             manager.createNotificationChannel(serviceChannel);
        }

    }

    private void biometricCheck() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            LocalDateTime curr = LocalDateTime.now();
            int hr = curr.getHour();
            int min = curr.getMinute();

            if((hr == 10 && min == 0) || (hr == 13 && min == 0) || (hr == 16 && min == 0)) {
                Intent dialogIntent = new Intent(this, BiometricActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
            }

        }

    }
}