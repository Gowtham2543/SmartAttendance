package com.example.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.location.LocationManagerCompat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    SharedPreferences sharedPreferences;
    OkHttpClient okHttpClient;
    String endpointURl = Endpoint.index;

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

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor1 = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor1.scheduleAtFixedRate(this::locationCheck, 0, 1, TimeUnit.SECONDS);

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

    private void locationCheck() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!LocationManagerCompat.isLocationEnabled(locationManager)) {

            sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);

            Request request = new Request.Builder().header("Authorization", "Bearer " + sharedPreferences.getString("accessToken", null)).url(endpointURl + "absent").build();

            okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.exit(0);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    System.exit(0);
                }
            });


        }
    }
}