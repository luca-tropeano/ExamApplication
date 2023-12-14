package com.example.examapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Verifica i permessi e richiedili se necessario
        if (checkPermissions()) {
            // Avvia l'handler dopo il ritardo
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Avvia la MainActivity dopo il ritardo
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                    // Chiudi la SplashActivity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    // Verifica i permessi
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.SET_ALARM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED

            };

            boolean allPermissionsGranted = true;

            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    // Permesso mancante, richiedilo all'utente solo se non è già stato negato in precedenza
                    if (shouldShowRequestPermissionRationale(permission)) {
                        Toast.makeText(this, "I permessi sono necessari per il corretto funzionamento dell'applicazione", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{permission}, PERMISSION_REQUEST_CODE);
                    allPermissionsGranted = false;
                }
            }

            return allPermissionsGranted;
        } else {
            // Versione di Android precedente a Marshmallow, i permessi sono inclusi nell'APK
            return true;
        }

    }

    // Gestisce la risposta alla richiesta di permessi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Permessi concessi, avvia l'handler dopo il ritardo
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Avvia SignUpActivity dopo il ritardo
                        Intent intent = new Intent(SplashActivity.this, SignupActivity.class);
                        startActivity(intent);

                        // Chiudi la SplashActivity
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            } else {
                // Permessi negati, esci dall'applicazione
                finish();
            }
        }
    }
}
