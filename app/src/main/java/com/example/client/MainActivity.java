package com.example.client;

import android.content.Context;
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
}