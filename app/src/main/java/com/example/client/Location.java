package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Location extends AppCompatActivity {
    TextView userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        userName = findViewById(R.id.userName);

        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("userName", "Hi"));
    }
}