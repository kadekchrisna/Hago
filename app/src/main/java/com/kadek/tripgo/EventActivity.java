package com.kadek.tripgo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EventActivity extends AppCompatActivity {

    private FloatingActionButton mAddEventFloatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mAddEventFloatButton = (FloatingActionButton) findViewById(R.id.addevent_button);


        mAddEventFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addEventIntent = new Intent(EventActivity.this, EventAddActivity.class);
                startActivity(addEventIntent);

            }
        });

    }
}
