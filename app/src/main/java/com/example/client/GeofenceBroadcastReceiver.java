package com.example.client;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    String endpointURl = "http://192.168.91.5:5000/";
    OkHttpClient okHttpClient;

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            System.out.println(errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            Toast.makeText(context, "Left Geofence", Toast.LENGTH_LONG).show();
            sharedPreferences = context.getSharedPreferences("userDetails", MODE_PRIVATE);
            Request request = new Request.Builder().header("Authorization", "Bearer " + sharedPreferences.getString("accessToken", null)).url(endpointURl + "out_time").build();

            okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                }
            });

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Toast.makeText(context, "Entered Geofence", Toast.LENGTH_LONG).show();
            sharedPreferences = context.getSharedPreferences("userDetails", MODE_PRIVATE);
            Request request = new Request.Builder().header("Authorization", "Bearer " + sharedPreferences.getString("accessToken", null)).url(endpointURl + "in_time").build();

            okHttpClient = new OkHttpClient();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                }
            });

        }

    }
}
