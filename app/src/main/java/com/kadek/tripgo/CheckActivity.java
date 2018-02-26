package com.kadek.tripgo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CheckActivity extends AppCompatActivity {

    private TextView mTextViewStatus;
    private FirebaseAuth mAuth;
    private Button mButtonSendEmail, mButtonCancel, mButtonCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check);

        mTextViewStatus = (TextView)findViewById(R.id.textView2);
        mButtonSendEmail = (Button)findViewById(R.id.button2);
        mButtonCancel = (Button)findViewById(R.id.button3);
        mButtonCheck = (Button)findViewById(R.id.button4);

        mAuth = FirebaseAuth.getInstance();


        mButtonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                currentUser.sendEmailVerification();


            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FirebaseAuth.getInstance().signOut();
                FirebaseUser user = mAuth.getCurrentUser();
                user.delete();
                sendToStart();
            }
        });

        mButtonCheck.setOnClickListener(new View.OnClickListener() {
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
            startActivity(mainIntent);
            finish();

        }else {

            mTextViewStatus.setText("Status: false");

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
}
