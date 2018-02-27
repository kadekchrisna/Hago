package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mButtonLogin;
    private TextInputLayout mInputEmail, mInputPassword;

    private ProgressDialog mProgressLogin;
    private Toolbar mToolbar;

    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar)findViewById(R.id.login_app_bar);

        mButtonLogin = (Button) findViewById(R.id.login_button);
        mInputEmail = (TextInputLayout) findViewById(R.id.login_textinputemail);
        mInputPassword = (TextInputLayout) findViewById(R.id.login_textinputpassword);

        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressLogin = new ProgressDialog(this);


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mInputEmail.getEditText().getText().toString();
                String password = mInputPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) ){
                    mProgressLogin.setTitle("Logging In");
                    mProgressLogin.setMessage("Please Wait...");
                    mProgressLogin.setCanceledOnTouchOutside(false);
                    mProgressLogin.show();
                    loginUser(email, password);
                }else {
                    Toast.makeText(LoginActivity.this, "PLease Fill Form Correctly", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){


                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String uid = currentUser.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("device_token");

                    mUserDatabase.setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mProgressLogin.dismiss();
                                Intent checkIntent = new Intent(LoginActivity.this, MainActivity.class);
                                checkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(checkIntent);
                                finish();

                            }else {
                                mProgressLogin.hide();
                                Toast.makeText(LoginActivity.this, "Please check the form and try again. ", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });




                }else{

                    mProgressLogin.hide();
                    Toast.makeText(LoginActivity.this, "Cannot Sign In. PLease check the form and try again. ", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}
