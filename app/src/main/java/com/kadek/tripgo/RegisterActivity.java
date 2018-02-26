package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button mReggisterButton;
    private TextInputLayout mInputName, mInputEmail, mInputPass;
    private Toolbar mToolbar;
    private DatabaseReference mUserDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.register_app_bar);
        mReggisterButton = (Button) findViewById(R.id.register_button);
        mInputEmail = (TextInputLayout) findViewById(R.id.register_textinputemail);
        mInputName = (TextInputLayout) findViewById(R.id.register_textinputname);
        mInputPass = (TextInputLayout) findViewById(R.id.register_textinputpass);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);




        mReggisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mInputName.getEditText().getText().toString();
                String email = mInputEmail.getEditText().getText().toString();
                String password = mInputPass.getEditText().getText().toString();


                if (!TextUtils.isEmpty(name) || (!TextUtils.isEmpty(email)) || (!TextUtils.isEmpty(password))){
                    mProgressDialog.setTitle("Registering New User");
                    mProgressDialog.setMessage("Please Wait");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    register_user(name, email, password);
                }


            }
        });

    }

    private void register_user(String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    String userUid = mAuth.getCurrentUser().getUid();


                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);

                    Intent checkIntent = new Intent(RegisterActivity.this, CheckActivity.class);
                    startActivity(checkIntent);

                }else {
                    Toast.makeText(RegisterActivity.this, "You got some Error. ", Toast.LENGTH_LONG).show();
                }

            }
        });

    }
}

