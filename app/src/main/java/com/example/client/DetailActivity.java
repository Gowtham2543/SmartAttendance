package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
    ImageView profile;
    TextView tName, tEmail, tDesignation, tAge;
    EditText editName, editEmail, editAge, editDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tName = findViewById(R.id.name);
        tEmail = findViewById(R.id.email);
        tDesignation = findViewById(R.id.designation);
        tAge = findViewById(R.id.age);

        profile = findViewById(R.id.profile);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editDesignation = findViewById(R.id.editDesignation);

        tName.setText("NAME");
        tEmail.setText("EMAIL");
        tAge.setText("AGE");
        tDesignation.setText("DESIGNATION");
    }
}