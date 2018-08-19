package com.kadek.tripgo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.Manifest;

public class SplashActivity extends AppCompatActivity {
    int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final String[] PERMISSIONS = {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        };



        Thread mThread = new Thread(){

            @Override
            public void run() {
                try {
                    sleep(2000);

                    if(!hasPermissions(SplashActivity.this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }else {
                        Intent welcomeIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                        startActivity(welcomeIntent);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

        mThread.run();






    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
