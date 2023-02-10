package com.example.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editUserName, editPassword;
    Button login;
    OkHttpClient okHttpClient;
    SharedPreferences sharedPreferences;
    String endpointURl = "http://192.168.91.4:5000/employee/";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        Request request = new Request.Builder().url(endpointURl + "login").post(body).build();
        okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseMsg = response.body().string();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(responseMsg);
                    if(jsonObject.getString("status").equals("Success")) {

                        sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", userName);
                        editor.putString("password", password);
                        editor.putString("accessToken", jsonObject.getString("access_token"));
                        editor.apply();
                        loginSuccess();

                    }
                    else {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), responseMsg, Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private void loginSuccess() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service");
        ContextCompat.startForegroundService(this, serviceIntent);

        Intent intent = new Intent(LoginActivity.this, DetailActivity.class);
        startActivity(intent);
    }

}