package com.kadek.tripgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AdminActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private Button mLogoutButton, mPlaceButton, mConvButton, mEventButton, mStuffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mToolbar = (Toolbar)findViewById(R.id.admin_app_bar);


        mEventButton = (Button) findViewById(R.id.account_event_button);
        mStuffButton = (Button) findViewById(R.id.account_stuff_button);
        mPlaceButton = (Button) findViewById(R.id.account_places);




        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Pengelola");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent eventIntent = new Intent(AdminActivity.this, EventActivity.class);
                startActivity(eventIntent);

            }
        });

        mStuffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, ProductActivity.class));
            }
        });
        mPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent placesIntent = new Intent(AdminActivity.this, PlaceActivity.class);
                startActivity(placesIntent);

            }
        });


    }
}
