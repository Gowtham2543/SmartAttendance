package com.example.client;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {
    ImageView profile;
    TextView tName, tEmail, tDesignation, tAge,
            displayName, displayEmail, displayAge, displayDesignation;
    private GeofencingClient geofencingClient;
    List<Geofence> geofenceList = new ArrayList<>();
    public PendingIntent geofencePendingIntent;
    OkHttpClient okHttpClient;
    SharedPreferences sharedPreferences;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    String endpointURl = "http://192.168.91.4:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tName = findViewById(R.id.name);
        tEmail = findViewById(R.id.email);
        tDesignation = findViewById(R.id.designation);
        tAge = findViewById(R.id.age);
        displayName = findViewById(R.id.displayName);
        displayEmail = findViewById(R.id.displayEmail);
        displayAge = findViewById(R.id.displayAge);
        displayDesignation = findViewById(R.id.displayDesignation);

        profile = findViewById(R.id.profile);

        tName.setText("NAME");
        tEmail.setText("EMAIL");
        tAge.setText("AGE");
        tDesignation.setText("DESIGNATION");

        setDetails();
        startGeofence();

    }

    private void setDetails() {
        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);

        String json = "{\"userName\":\"" + sharedPreferences.getString("userName", null) + "\"}";
        System.out.println(json);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder().url(endpointURl + "list").post(body).build();


        okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseMsg = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(responseMsg);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(() -> {
                    try {
                        displayEmail.setText(jsonObject.getString("email"));
                        displayAge.setText(jsonObject.getString("age"));
                        displayName.setText(jsonObject.getString("username"));
                        displayDesignation.setText(jsonObject.getString("designation"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        });
    }

    private void startGeofence() {

        geofencingClient = LocationServices.getGeofencingClient(this);

        geofenceList.add(new Geofence.Builder()
                .setRequestId("GEOFENCE LOCATION")
                .setCircularRegion(
                        11.025109,
                        77.028585,
                        2
                )
                .setLoiteringDelay(1000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()
        );

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, aVoid -> {
                        // Geofences added
                        // ...
                        System.out.println("Added geofence");
                    })
                    .addOnFailureListener(this, e -> {

                        System.out.println("Failed to add Geofence");
                    });
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 1);

        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);

        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        return geofencePendingIntent;
    }
}