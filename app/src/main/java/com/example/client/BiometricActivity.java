package com.example.client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BiometricActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    SharedPreferences sharedPreferences;
    OkHttpClient okHttpClient;
    String endpointURl = "http://192.168.91.4:5000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(BiometricActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                Request request = new Request.Builder().header("Authorization", "Bearer " + sharedPreferences.getString("accessToken", null)).url(endpointURl + "present").build();

                okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {

                    }
                });

                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT).show();
                sharedPreferences = getSharedPreferences("userDetails", MODE_PRIVATE);
                Request request = new Request.Builder().header("Authorization", "Bearer " + sharedPreferences.getString("accessToken", null)).url(endpointURl + "absent").build();

                okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        finish();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        finish();
                    }
                });

                finish();
            }

        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setNegativeButtonText(".")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

}