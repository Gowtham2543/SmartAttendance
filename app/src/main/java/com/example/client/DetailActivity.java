package com.example.client;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    ImageView profile;
    TextView tName, tEmail, tDesignation, tAge,
            displayName, displayEmail, displayAge, displayDesignation;

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

        // Code for receiving details and displaying


    }
}