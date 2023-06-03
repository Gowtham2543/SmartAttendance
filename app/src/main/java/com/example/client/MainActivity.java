package com.example.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    static final int LOCATION_PERMISSION_CODE = 400;
    static final int BACKGROUND_LOCATION_PERMISSION_CODE = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();


    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here
                    success();
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage();
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission();
        }
    }

    private void askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Location Permission Needed!")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE))
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                        // Permission is denied by the user
                        Toast.makeText(this, "Location permission needed", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private void askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                    .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE))
                    .setNegativeButton("CANCEL", (dialog, which) -> {
                        // User declined for Background Location Permission.
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Background Location Permission is granted so do your work here
                        success();
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "The app requires the location permission", Toast.LENGTH_LONG).show();
                finish();
                // User denied location permission
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                success();
                // User granted for Background Location Permission.
            } else {
                Toast.makeText(MainActivity.this, "The app requires the location permission", Toast.LENGTH_LONG).show();
                finish();
                // User declined for Background Location Permission.

            }
        }

    }

    private void success() {
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MainActivity. this )
                    .setMessage( "Please enable the GPS and restart the app")
                    .setPositiveButton( "Settings" , (paramDialogInterface, paramInt) -> startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )))
                    .setNegativeButton( "Cancel" , null )
                    .show() ;
        }

        if(gps_enabled && network_enabled) {
            sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
            if(sharedPreferences.contains("accessToken")) {

                Intent serviceIntent = new Intent(MainActivity.this, ForegroundService.class);
                serviceIntent.putExtra("inputExtra", "Foreground Service");
                ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
        else {
            Toast.makeText(MainActivity.this, "The app requires GPS to be enabled", Toast.LENGTH_LONG).show();
        }
    }
}