package com.kadek.tripgo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class EditPlacesActivity extends AppCompatActivity {

    private final static int PLACE_PICKER_REQUEST = 2;

    private Toolbar mToolbar;
    private FirebaseUser currentUser;

    private Spinner mDropdown;
    private Button mLatlongButton, mSaveButton, mCancelButton;
    private TextView mTextviewPlaceName;
    private DatabaseReference mPlacesDatabase, mUserPlaceDatabase;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage;
    private static final int GALLERY_PICK = 1;
    private ImageButton mImageButton1, mImageButton2, mImageButton3, mImageButton4;
    private TextInputLayout mInputName, mInputPhone, mInputPrice, mInputDescription, mInputYoutubeId;
    private LatLng mLatlong;
    private ArrayAdapter<CharSequence> mArrayAdapter;
    private String mCategory;
    private String placeUid = FirebaseDatabase.getInstance().getReference().child("Places").push().getKey();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_places);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser.getUid();

        mPlacesDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(placeUid);
        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mImageButton1 = (ImageButton) findViewById(R.id.edit_imagebutton1);
        mImageButton2 = (ImageButton) findViewById(R.id.edit_imagebutton2);
        mImageButton3 = (ImageButton) findViewById(R.id.edit_imagebutton3);
        mImageButton4 = (ImageButton) findViewById(R.id.edit_imagebutton4);

        mDropdown = (Spinner) findViewById(R.id.edit_spinner_category);
        mArrayAdapter = ArrayAdapter.createFromResource(this, R.array.category, android.R.layout.simple_spinner_dropdown_item);
        mInputName = (TextInputLayout) findViewById(R.id.edit_input_nameplace);
        mInputPhone = (TextInputLayout) findViewById(R.id.edit_input_phone);
        mInputPrice = (TextInputLayout) findViewById(R.id.edit_input_price);
        mInputDescription = (TextInputLayout) findViewById(R.id.edit_input_description);
        mInputYoutubeId = (TextInputLayout) findViewById(R.id.edit_input_youtube);

        mSaveButton = (Button) findViewById(R.id.edit_button_save);
        mCancelButton = (Button) findViewById(R.id.edit_button_cancel);
        mLatlongButton = (Button) findViewById(R.id.edit_button_addlatong);
        mTextviewPlaceName = (TextView) findViewById(R.id.edit_textview_placesname);
        mToolbar = (Toolbar) findViewById(R.id.edit_places_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Places");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDropdown.setAdapter(mArrayAdapter);
        mDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mCategory  = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(EditPlacesActivity.this, AddPlaceActivity.class);
                startActivity(backIntent);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

                String name = mInputName.getEditText().getText().toString();
                String phone = mInputPhone.getEditText().getText().toString();
                String price = mInputPrice.getEditText().getText().toString();
                String description = mInputDescription.getEditText().getText().toString();
                String youtube = mInputYoutubeId.getEditText().getText().toString();
                String latlong = mLatlong.toString();
                String category = mCategory;

                Map update_hashMap = new HashMap();
                update_hashMap.put("name", name);
                update_hashMap.put("phone", phone);
                update_hashMap.put("price", price);
                update_hashMap.put("description", description);
                update_hashMap.put("youtube", youtube);
                update_hashMap.put("latlong", latlong);
                update_hashMap.put("category", category);


                mPlacesDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){

                            mUserPlaceDatabase.child(placeUid).child("places").setValue(placeUid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                        Intent backIntent = new Intent(EditPlacesActivity.this, AddPlaceActivity.class);
                                        startActivity(backIntent);
                                        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        finish();

                                }
                            });

                        }else {
                            Toast.makeText(EditPlacesActivity.this, "Error Uploading", Toast.LENGTH_SHORT).show();
                            mProgressDialog.hide();
                        }

                    }
                });



            }
        });
        mImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"),GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST){

            if (resultCode==RESULT_OK){

                Place place = PlacePicker.getPlace(EditPlacesActivity.this, data);
                mTextviewPlaceName.setText(place.getLatLng().toString());
                mLatlong = place.getLatLng();


            }

        }

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(2,1)
                    .start(EditPlacesActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();

                File thumbFilePath = new File(resultUri.getPath());
                String current_user_id = currentUser.getUid();

                Bitmap thumb_bitmap = null;
                thumb_bitmap = new Compressor(this)
                        .setMaxHeight(720)
                        .setMaxWidth(1080)
                        .setQuality(75)
                        .compressToBitmap(thumbFilePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("places_images").child(current_user_id).child(placeUid + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("places_images").child("thumbs").child(current_user_id).child(placeUid + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image", download_url);
                                        update_hashMap.put("thumb_image", thumb_downloadUrl);

                                        mPlacesDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                                    mProgressDialog.dismiss();

                                                }
                                            }
                                        });

                                    }else {
                                        Toast.makeText(EditPlacesActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }

                                }
                            });



                        }else{
                            Toast.makeText(EditPlacesActivity.this, "Error in uploading1", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


}
