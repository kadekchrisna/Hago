package com.kadek.tripgo;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public class EditPlacesActivity extends AppCompatActivity {

    private final static int PLACE_PICKER_REQUEST = 1;

    private Button mLatlongButton;
    private TextView mTextviewPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_places);

        mLatlongButton = (Button) findViewById(R.id.edit_button_addlatong);
        mTextviewPlaceName = (TextView) findViewById(R.id.edit_textview_placesname);


        mLatlongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(EditPlacesActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesNotAvailableException e1) {
                    e1.printStackTrace();
                } catch (GooglePlayServicesRepairableException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST){

            if (resultCode==RESULT_OK){

                Place place = PlacePicker.getPlace(EditPlacesActivity.this, data);
                mTextviewPlaceName.setText(place.getLatLng().toString());
                LatLng mLatlong = place.getLatLng();


            }

        }

    }
}
