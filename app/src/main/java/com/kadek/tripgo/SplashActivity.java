package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {




    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null){

                    if (mAuth.getCurrentUser().getUid() != null){
                        final FirebaseUser currentUser = mAuth.getCurrentUser();
                        final String uid = currentUser.getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        Map userMap = new HashMap<>();
                        userMap.put("name", currentUser.getDisplayName());
                        userMap.put("image", currentUser.getPhotoUrl().toString());
                        userMap.put("thumb_image", currentUser.getPhotoUrl().toString());
                        userMap.put("device_token", deviceToken);
                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                        mUserDatabase.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    //mProgressDialog.dismiss();
                                    Intent checkIntent = new Intent(SplashActivity.this, MainActivity.class);
                                    checkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP & Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(checkIntent);
                                    finish();

                                }else{
                                    //mProgressDialog.hide();
                                    Toast.makeText(SplashActivity.this, "Please check internet connection and try again.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });




                    }else {
                        Intent checkIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                        checkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(checkIntent);

                    }


                }else {
                    Intent checkIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    checkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(checkIntent);

                }

            }
        };


    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);





    }

}
