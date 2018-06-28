package com.kadek.tripgo;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static android.widget.ImageView.ScaleType.FIT_XY;

public class EventAddActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mEventName, mEventDescription;
    private Button mSave, mCancel;
    private ImageButton mEventImage;

    private int GALLERY_PICK;

    private DatabaseReference mEventDatabase, mEventOwnerDatabase;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage, mImageDelete;

    private FirebaseUser currentUser;

    private Boolean clicked = false;
    private String downloadUrl, thumb_downloadUrl;
    private EditText mStart, mEnd;
    private Calendar myCalendar, myCalendarEnd;

    private String eventUid = FirebaseDatabase.getInstance().getReference().child("Event").push().getKey();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        myCalendar = Calendar.getInstance();
        myCalendarEnd = Calendar.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.event_add_app_bar);
        mEventName = (TextInputLayout) findViewById(R.id.event_input_eventname);
        mEventDescription = (TextInputLayout) findViewById(R.id.event_input_description);
        mEventImage = (ImageButton) findViewById(R.id.event_imagebutton1);
        mSave = (Button) findViewById(R.id.event_button_save);
        mCancel = (Button) findViewById(R.id.event_button_cancel);

        mStart = (EditText) findViewById(R.id.event_date_start);
        mEnd = (EditText) findViewById(R.id.event_date_end);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tambah Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUid = currentUser.getUid();

        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event").child(eventUid);
        mEventOwnerDatabase = FirebaseDatabase.getInstance().getReference().child("EventOwner").child(currentUid).child(eventUid);

        mImageStorage = FirebaseStorage.getInstance().getReference();


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        final DatePickerDialog.OnDateSetListener dateEnd = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, monthOfYear);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelEnd();
            }

        };

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EventAddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EventAddActivity.this, dateEnd, myCalendarEnd
                        .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog = new ProgressDialog(EventAddActivity.this);
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

                String name = mEventName.getEditText().getText().toString();
                String description = mEventDescription.getEditText().getText().toString();
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                String start = mStart.getText().toString();
                String end = mEnd.getText().toString();

                Map eventMap = new HashMap();
                eventMap.put("name", name);
                eventMap.put("description", description);
                eventMap.put("event_start", start);
                eventMap.put("event_end", end);
                eventMap.put("timestamp", currentDateTimeString);
                eventMap.put("event_image", downloadUrl);
                eventMap.put("event_thumb_image", thumb_downloadUrl);

                final Map eventOwn = new HashMap();
                eventOwn.put("event",eventUid);




                mEventDatabase.updateChildren(eventMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){

                            mEventOwnerDatabase.updateChildren(eventOwn).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {


                                        Toast.makeText(EventAddActivity.this, "Success Uploading", Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();
                                        Intent backIntent = new Intent(EventAddActivity.this, EventActivity.class);
                                        startActivity(backIntent);
                                        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        finish();

                                    }

                                }
                            });

                        }else {
                            mProgressDialog.hide();
                            Toast.makeText(EventAddActivity.this, "Please check intenet connection and try again. ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });



            }
        });


        mEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clicked == false) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    GALLERY_PICK = 1;
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);


                }else {
                    mProgressDialog = new ProgressDialog(EventAddActivity.this);
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


                                                            mEventDatabase.child("event_image").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        mEventDatabase.child("event_thumb_image").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                                mProgressDialog.dismiss();
                                                                                clicked = false;
                                                                                mEventImage.setImageResource(R.drawable.baseline_add_24);


                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });




                                                        }else {
                                                            mProgressDialog.hide();
                                                            Toast.makeText(EventAddActivity.this, "Please check internet connection and try again. ", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });

                                    }else {
                                        mProgressDialog.hide();
                                        Toast.makeText(EventAddActivity.this, "Please check internet connection and try again.1 ", Toast.LENGTH_SHORT).show();
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

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(2, 1)
                    .start(EventAddActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(EventAddActivity.this);
                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();

                File thumbFilePath = new File(resultUri.getPath()); //file path image
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
                if (GALLERY_PICK == 1) {
                    StorageReference filepath = mImageStorage.child("event_images").child(current_user_id).child(eventUid + ".jpg");
                    final StorageReference thumb_filepath = mImageStorage.child("event_images").child("thumbs").child(current_user_id).child(eventUid + ".jpg");
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

                                            Toast.makeText(EventAddActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();
                                            mEventImage.setScaleType(FIT_XY);
                                            Picasso.with(EventAddActivity.this).load(thumb_downloadUrl).into(mEventImage);
                                            clicked = true;

                                        } else {
                                            Toast.makeText(EventAddActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                            mProgressDialog.dismiss();

                                        }

                                    }
                                });

                            } else {
                                Toast.makeText(EventAddActivity.this, "Error in uploading1", Toast.LENGTH_LONG).show();
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
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mStart.setText(sdf.format(myCalendar.getTime()));
    }
    private void updateLabelEnd() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mEnd.setText(sdf.format(myCalendarEnd.getTime()));
    }
}
