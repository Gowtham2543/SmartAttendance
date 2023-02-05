package com.example.client;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    ImageView profile;
    TextView tName, tEmail, tDesignation, tAge,
            displayName, displayEmail, displayAge, displayDesignation;

    private GeofencingClient geofencingClient;
    List<Geofence> geofenceList = new ArrayList<>();

    public PendingIntent geofencePendingIntent;

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


        startGeofence();

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
                        System.out.println("Success");
                    })
                    .addOnFailureListener(this, e -> {
                        // Failed to add geofences
                        // ...
                        System.out.println("uryuryutr");
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