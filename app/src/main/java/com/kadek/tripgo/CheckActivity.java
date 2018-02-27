package com.kadek.tripgo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckActivity extends AppCompatActivity {

    private TextView mTextViewStatus;
    private FirebaseAuth mAuth;
    private Button mButtonCancel, mButtonRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check);

        mTextViewStatus = (TextView)findViewById(R.id.check_textstatus);
        mButtonCancel = (Button)findViewById(R.id.check_buttoncancel);
        mButtonRefresh = (Button)findViewById(R.id.check_buttonrefresh);
        mTextViewStatus.setText("Status: false");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.sendEmailVerification();




        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FirebaseAuth.getInstance().signOut();
                FirebaseUser user = mAuth.getCurrentUser();
                user.delete();
                sendToStart();
            }
        });

        mButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setInfo();
                    }
                });
            }
        });


    }

    private void setInfo() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser.isEmailVerified()){

            mTextViewStatus.setText(new StringBuilder("Status: ").append(String.valueOf(currentUser.isEmailVerified())));
            Intent mainIntent = new Intent(CheckActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

        }else {

            Toast.makeText(this, "Please Check Your Email. ", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){

            sendToStart();


        }

    }

    private void sendToStart() {

        Intent redirectIntent = new Intent(this, WelcomeActivity.class);
        startActivity(redirectIntent);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser.isEmailVerified()){

            Toast.makeText(this, "User Verified", Toast.LENGTH_SHORT).show();
            Intent checkIntent = new Intent(CheckActivity.this, MainActivity.class);
            startActivity(checkIntent);
            finish();

        }else{

            Toast.makeText(this, "Please Click The Refresh Button", Toast.LENGTH_SHORT).show();

        }

    }
}
