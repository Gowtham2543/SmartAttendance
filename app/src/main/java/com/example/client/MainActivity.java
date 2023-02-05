package com.example.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    OkHttpClient okHttpClient;
    SharedPreferences sharedPreferences;
    String endpointURl = "http://192.168.142.5:5000/";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        okHttpClient = new OkHttpClient();
        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        if(sharedPreferences.contains("userName") && sharedPreferences.contains("password")) {
            String json = "{\"userName\":\"" + sharedPreferences.getString("userName", null) + "\",\"password\":\""
                    + sharedPreferences.getString("password", null) + "\"}";
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder().url(endpointURl + "login").post(body).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseMessage = response.body().string();
                    if(responseMessage.equals("Success")) {

                        loginSuccess();
                    }
                    else {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    private void loginSuccess() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service");
        ContextCompat.startForegroundService(this, serviceIntent);

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        startActivity(intent);
    }

}