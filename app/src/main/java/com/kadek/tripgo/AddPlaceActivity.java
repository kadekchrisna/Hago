package com.kadek.tripgo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddPlaceActivity extends AppCompatActivity {

    private FloatingActionButton mAddPlaceFloatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        mAddPlaceFloatButton = (FloatingActionButton) findViewById(R.id.addplace_button);


        mAddPlaceFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent = new Intent(AddPlaceActivity.this, EditPlacesActivity.class);
                startActivity(editIntent);

            }
        });

    }
}
