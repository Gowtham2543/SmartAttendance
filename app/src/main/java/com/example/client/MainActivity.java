package com.example.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText editUserName, editPassword;
    Button login;
    OkHttpClient okHttpClient;
    SharedPreferences sharedPreferences;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        okHttpClient = new OkHttpClient();
        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
        if(sharedPreferences.contains("userName") && sharedPreferences.contains("password")) {
            String json = "{\"userName\":\"" + sharedPreferences.getString("userName", null) + "\",\"password\":\""
                    + sharedPreferences.getString("password", null) + "\"}";
            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder().url("http://192.168.191.4:5000/login").post(body).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseMessage = response.body().string();
                    if(responseMessage.equals("Success")) {
                        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUserName = findViewById(R.id.editUserName);
        editPassword = findViewById(R.id.editPassword);
        login = findViewById(R.id.login);

        login.setOnClickListener(v -> onPressLogin());
    }

    protected void onPressLogin()  {
        String userName = String.valueOf(editUserName.getText());
        String password = String.valueOf(editPassword.getText());

        String json = "{\"userName\":\"" + userName + "\",\"password\":\"" + password + "\"}";
        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder().url("http://192.168.229.5:5000/login").post(body).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseMessage = response.body().string();
                if(responseMessage.equals("Success")) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userName", userName);
                    editor.putString("password", password);
                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                    startActivity(intent);
                }
                else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), responseMessage, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

}