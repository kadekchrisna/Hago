package com.kadek.tripgo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mLogoutButton;

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        homeFragment = new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();

        setFragment(homeFragment);


        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_notif:
                        mMainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(notificationFragment);
                        return true;

                    case R.id.nav_account:
                        mMainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        setFragment(accountFragment);
                        return true;

                        default:
                            return false;

                }
            }
        });


    }

    private void setFragment(android.support.v4.app.Fragment fragment) {

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser !=null){

            Toast.makeText(this, "User Verified", Toast.LENGTH_SHORT).show();

            /*if (currentUser.isEmailVerified() == true){

                Toast.makeText(this, "User Verified", Toast.LENGTH_SHORT).show();

            }else{

                Intent checkIntent = new Intent(MainActivity.this, CheckActivity.class);
                checkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(checkIntent);
                finish();

            }*/


        }else {

            Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(welcomeIntent);
            finish();

        }
    }

    private void sendToStart() {

        Intent redirectIntent = new Intent(this, WelcomeActivity.class);
        redirectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(redirectIntent);
        finish();
    }
}
