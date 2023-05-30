package com.example.client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
            Toast.makeText(MainActivity.this, "The app requires GPS to be enabled", Toast.LENGTH_LONG);
        }
    }
}