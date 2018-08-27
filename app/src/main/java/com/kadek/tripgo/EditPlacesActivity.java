package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

import static android.widget.ImageView.ScaleType.FIT_XY;

public class EditPlacesActivity extends AppCompatActivity {

    private final static int PLACE_PICKER_REQUEST = 9;

    private Toolbar mToolbar;

    private Spinner mDropdown;
    private Button mLatlongButton, mSaveButton, mCancelButton;
    private TextView mTextviewPlaceName;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage, mImageDelete;
    private int GALLERY_PICK;
    private ImageButton mImageButton1, mImageButton2, mImageButton3, mImageButton4;
    private TextInputLayout mInputName, mInputPhone, mInputPrice, mInputDescription, mInputYoutubeId;
    private LatLng mLatlong;
    private Double mLongitude, mLatitude;
    private ArrayAdapter<CharSequence> mArrayAdapter;
    private String mCategory;
    private DatabaseReference mPlaceDatabase, mUserPlaceDatabase;
    private FirebaseUser mCurrentUser;
    private String currentUid;

    public void placeUid(String placeUid) {
        this.placeUid = placeUid;
    }

    public String getPlaceUid() {
        return placeUid;
    }

    public void setPlaceUid(String placeUid) {
        this.placeUid = placeUid;
    }

    private String placeUid;



    private Boolean clicked = true;
    private Boolean clicked2 = true;
    private Boolean clicked3 = true;
    private Boolean clicked4 = true;
    private String downloadUrl, thumb_downloadUrl, downloadUrl2, thumb_downloadUrl2, downloadUrl3, thumb_downloadUrl3, downloadUrl4, thumb_downloadUrl4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_places2);

        mInputName = (TextInputLayout) findViewById(R.id.edit_input_nameplace);
        mInputPrice = (TextInputLayout) findViewById(R.id.edit_input_price);
        mInputPhone = (TextInputLayout) findViewById(R.id.edit_input_phone);
        mInputYoutubeId = (TextInputLayout) findViewById(R.id.edit_input_youtube);
        mInputDescription = (TextInputLayout) findViewById(R.id.edit_input_description);
        mTextviewPlaceName = (TextView) findViewById(R.id.edit_textview_placesname);

        mImageButton1 = (ImageButton) findViewById(R.id.edit_imagebutton1);
        mImageButton2 = (ImageButton) findViewById(R.id.edit_imagebutton2);
        mImageButton3 = (ImageButton) findViewById(R.id.edit_imagebutton3);
        mImageButton4 = (ImageButton) findViewById(R.id.edit_imagebutton4);

        mLatlongButton = (Button) findViewById(R.id.edit_button_addlatong);
        mSaveButton = (Button) findViewById(R.id.edit_button_save);
        mCancelButton = (Button) findViewById(R.id.edit_button_cancel);

        mDropdown = (Spinner) findViewById(R.id.edit_spinner_category);

        mArrayAdapter = ArrayAdapter.createFromResource(EditPlacesActivity.this, R.array.category, android.R.layout.simple_spinner_dropdown_item);

        mToolbar = (Toolbar) findViewById(R.id.edit_places_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Tempat Wisata");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String placeUid = getIntent().getStringExtra("place_id");

        setPlaceUid(placeUid);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();



        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(placeUid);
        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mDropdown.setAdapter(mArrayAdapter);




        mPlaceDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String category = dataSnapshot.child("category").getValue().toString();
                    String youtube = dataSnapshot.child("youtubelink").getValue().toString();
                    String latitude = dataSnapshot.child("latitude").getValue().toString();
                    String longitude = dataSnapshot.child("longitude").getValue().toString();
                    downloadUrl = dataSnapshot.child("image").getValue().toString();
                    thumb_downloadUrl = dataSnapshot.child("thumb_image").getValue().toString();
                    downloadUrl2 = dataSnapshot.child("image2").getValue().toString();
                    thumb_downloadUrl2 = dataSnapshot.child("thumb_image2").getValue().toString();
                    downloadUrl3 = dataSnapshot.child("image3").getValue().toString();
                    thumb_downloadUrl3 = dataSnapshot.child("thumb_image3").getValue().toString();
                    downloadUrl4 = dataSnapshot.child("image4").getValue().toString();
                    thumb_downloadUrl4 = dataSnapshot.child("thumb_image4").getValue().toString();


                    mLatitude = Double.parseDouble(latitude);
                    mLongitude = Double.parseDouble(longitude);

                    mInputName.getEditText().setText(name);
                    mInputPrice.getEditText().setText(price);
                    mInputPhone.getEditText().setText(phone);
                    mInputYoutubeId.getEditText().setText(youtube);
                    mInputDescription.getEditText().setText(description);
                    int spinnerLocation = mArrayAdapter.getPosition(category);
                    mDropdown.setSelection(spinnerLocation);
                    mTextviewPlaceName.setText(latitude + "," + longitude);

                    mImageButton1.setScaleType(FIT_XY);
                    Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl).into(mImageButton1);

                    mImageButton2.setScaleType(FIT_XY);
                    Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl2).into(mImageButton2);

                    mImageButton3.setScaleType(FIT_XY);
                    Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl3).into(mImageButton3);

                    mImageButton4.setScaleType(FIT_XY);
                    Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl4).into(mImageButton4);

                }







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

                AlertDialog.Builder builder = new AlertDialog.Builder(EditPlacesActivity.this);
                builder.setMessage("Apakah anda yakin untuk keluar ?");
                builder.setCancelable(false);
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });
                builder.setPositiveButton("Ya!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent backIntent = new Intent(EditPlacesActivity.this, PlaceActivity.class);
                        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(backIntent);
                        finish();

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInputName.getEditText().getText().toString().isEmpty() || mInputPhone.getEditText().getText().toString().isEmpty() ||
                        mInputPrice.getEditText().getText().toString().isEmpty() || mInputDescription.getEditText().getText().toString().isEmpty() ||
                        mInputYoutubeId.getEditText().getText().toString().isEmpty() || mCategory.equals("Pilih Kategori") ||
                        mLatitude.equals("") || mLongitude.equals("") || thumb_downloadUrl.isEmpty() || thumb_downloadUrl2.isEmpty() ||
                        thumb_downloadUrl3.isEmpty() || thumb_downloadUrl4.isEmpty()){
                    Toast.makeText(EditPlacesActivity.this, "Tolong lengkapi dan check from kembali. ", Toast.LENGTH_SHORT).show();

                }else{
                    mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                    mProgressDialog.setTitle("Saving Changes");
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.show();

                    String name = mInputName.getEditText().getText().toString();
                    String phone = mInputPhone.getEditText().getText().toString();
                    String price = mInputPrice.getEditText().getText().toString();
                    String description = mInputDescription.getEditText().getText().toString();
                    String youtube = mInputYoutubeId.getEditText().getText().toString();

                    String category = mCategory;

                    String mYoutube = getYouTubeId(youtube);

                    Map update_hashMap = new HashMap();
                    update_hashMap.put("name", name);
                    update_hashMap.put("phone", phone);
                    update_hashMap.put("price", price);
                    update_hashMap.put("description", description);
                    update_hashMap.put("youtube", mYoutube);
                    update_hashMap.put("youtubelink", youtube);
                    update_hashMap.put("latitude", mLatitude);
                    update_hashMap.put("longitude", mLongitude);
                    update_hashMap.put("category", category);
                    update_hashMap.put("owner", currentUid);

                    final Map update_hashMap_owner = new HashMap();
                    update_hashMap_owner.put("place", placeUid);



                    mPlaceDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()){

                                mUserPlaceDatabase.child(placeUid).updateChildren(update_hashMap_owner).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                        Intent backIntent = new Intent(EditPlacesActivity.this, PlaceActivity.class);
                                        startActivity(backIntent);
                                        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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





            }
        });

        mImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked == false) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    GALLERY_PICK = 1;
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


                }else {
                    mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                    mProgressDialog.setTitle("Deleting Image");
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl);
                    mImageDelete.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(thumb_downloadUrl);
                                        mImageDelete.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){


                                                            mPlaceDatabase.child("image").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        mPlaceDatabase.child("thumb_image").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                mProgressDialog.dismiss();
                                                                                clicked = false;
                                                                                mImageButton1.setImageResource(R.drawable.baseline_add_24);


                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });




                                                        }else {
                                                            mProgressDialog.hide();
                                                            Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again. ", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                    }else {
                                        mProgressDialog.hide();
                                        Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again.1 ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }
            }
        });

        mImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked2 == false) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    GALLERY_PICK = 2;
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


                }else {
                    mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                    mProgressDialog.setTitle("Deleting Image");
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl2);
                    mImageDelete.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(thumb_downloadUrl2);
                                        mImageDelete.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){


                                                            mPlaceDatabase.child("image2").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        mPlaceDatabase.child("thumb_image2").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                mProgressDialog.dismiss();
                                                                                clicked2 = false;
                                                                                mImageButton2.setImageResource(R.drawable.baseline_add_24);


                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });




                                                        }else {
                                                            mProgressDialog.hide();
                                                            Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again. ", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                    }else {
                                        mProgressDialog.hide();
                                        Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again.1 ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }

            }
        });

        mImageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked3 == false) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    GALLERY_PICK = 3;
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


                }else {
                    mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                    mProgressDialog.setTitle("Deleting Image");
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl3);
                    mImageDelete.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(thumb_downloadUrl3);
                                        mImageDelete.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){


                                                            mPlaceDatabase.child("image3").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        mPlaceDatabase.child("thumb_image3").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                mProgressDialog.dismiss();
                                                                                clicked3 = false;
                                                                                mImageButton3.setImageResource(R.drawable.baseline_add_24);


                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });




                                                        }else {
                                                            mProgressDialog.hide();
                                                            Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again. ", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                    }else {
                                        mProgressDialog.hide();
                                        Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again.1 ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }

            }
        });
        mImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked4 == false) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    GALLERY_PICK = 4;
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


                }else {
                    mProgressDialog = new ProgressDialog(EditPlacesActivity.this);
                    mProgressDialog.setTitle("Deleting Image");
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl4);
                    mImageDelete.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(thumb_downloadUrl4);
                                        mImageDelete.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){


                                                            mPlaceDatabase.child("image4").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        mPlaceDatabase.child("thumb_image4").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                mProgressDialog.dismiss();
                                                                                clicked4 = false;
                                                                                mImageButton4.setImageResource(R.drawable.baseline_add_24);


                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });




                                                        }else {
                                                            mProgressDialog.hide();
                                                            Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again. ", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                    }else {
                                        mProgressDialog.hide();
                                        Toast.makeText(EditPlacesActivity.this, "Please check internet connection and try again.1 ", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }

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
                mLatitude = mLatlong.latitude;
                mLongitude = mLatlong.longitude;
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

                File thumbFilePath = new File(resultUri.getPath()); //file path image
                String current_user_id = currentUid;

                Bitmap thumb_bitmap = null;
                thumb_bitmap = new Compressor(this)
                        .setMaxHeight(720)
                        .setMaxWidth(1080)
                        .setQuality(75)
                        .compressToBitmap(thumbFilePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                final byte[] thumb_byte = baos.toByteArray();
                if (GALLERY_PICK == 1) {
                    StorageReference filepath = mImageStorage.child("places_images").child(current_user_id).child(placeUid + ".jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("places_images").child("thumbs").child(current_user_id).child(placeUid + ".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                downloadUrl = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                        thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                        if (thumb_task.isSuccessful()) {

                                            Map update_hashMap = new HashMap();
                                            update_hashMap.put("image", downloadUrl);
                                            update_hashMap.put("thumb_image", thumb_downloadUrl);
                                            mPlaceDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                                        mProgressDialog.dismiss();
                                                        mImageButton1.setScaleType(FIT_XY);
                                                        Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl).into(mImageButton1);
                                                        clicked = true;

                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(EditPlacesActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();

                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(EditPlacesActivity.this, "Error in uploading1", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });
                }if (GALLERY_PICK == 2){
                    StorageReference filepath = mImageStorage.child("places_images").child(current_user_id).child(placeUid + "2.jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("places_images").child("thumbs").child(current_user_id).child(placeUid + "2.jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                downloadUrl2 = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        thumb_downloadUrl2 = thumb_task.getResult().getDownloadUrl().toString();

                                        if (thumb_task.isSuccessful()) {

                                            Map update_hashMap = new HashMap();
                                            update_hashMap.put("image2", downloadUrl2);
                                            update_hashMap.put("thumb_image2", thumb_downloadUrl2);

                                            mPlaceDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                                        mProgressDialog.dismiss();
                                                        mImageButton2.setScaleType(FIT_XY);
                                                        Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl2).into(mImageButton2);
                                                        clicked2 = true;

                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(EditPlacesActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();

                                        }

                                    }
                                });


                            } else {
                                Toast.makeText(EditPlacesActivity.this, "Error in uploading1", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });

                }if (GALLERY_PICK == 3){
                    StorageReference filepath = mImageStorage.child("places_images").child(current_user_id).child(placeUid + "3.jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("places_images").child("thumbs").child(current_user_id).child(placeUid + "3.jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                downloadUrl3 = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        thumb_downloadUrl3 = thumb_task.getResult().getDownloadUrl().toString();

                                        if (thumb_task.isSuccessful()) {

                                            Map update_hashMap = new HashMap();
                                            update_hashMap.put("image3", downloadUrl3);
                                            update_hashMap.put("thumb_image3", thumb_downloadUrl3);

                                            mPlaceDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                                        mProgressDialog.dismiss();
                                                        mImageButton3.setScaleType(FIT_XY);
                                                        Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl3).into(mImageButton3);
                                                        clicked3 = true;

                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(EditPlacesActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();

                                        }

                                    }
                                });


                            } else {
                                Toast.makeText(EditPlacesActivity.this, "Error in uploading1", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });

                }if (GALLERY_PICK == 4){
                    StorageReference filepath = mImageStorage.child("places_images").child(current_user_id).child(placeUid + "4.jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("places_images").child("thumbs").child(current_user_id).child(placeUid + "4.jpg");

                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()) {
                                downloadUrl4 = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        thumb_downloadUrl4 = thumb_task.getResult().getDownloadUrl().toString();

                                        if (thumb_task.isSuccessful()) {

                                            Map update_hashMap = new HashMap();
                                            update_hashMap.put("image4", downloadUrl4);
                                            update_hashMap.put("thumb_image4", thumb_downloadUrl4);

                                            mPlaceDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(EditPlacesActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                                        mProgressDialog.dismiss();
                                                        mImageButton4.setScaleType(FIT_XY);
                                                        Picasso.with(EditPlacesActivity.this).load(thumb_downloadUrl4).into(mImageButton4);
                                                        clicked4 = true;

                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(EditPlacesActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();

                                        }

                                    }
                                });


                            } else {
                                Toast.makeText(EditPlacesActivity.this, "Error in uploading1", Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });

                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPlacesActivity.this);
        builder.setMessage("Apakah anda yakin untuk keluar ?");
        builder.setCancelable(false);
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();

            }
        });
        builder.setPositiveButton("Ya!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent backIntent = new Intent(EditPlacesActivity.this, PlaceActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
                finish();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
