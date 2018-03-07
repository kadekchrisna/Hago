package com.kadek.tripgo;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.ImageView.ScaleType.FIT_XY;

public class DetailActivity extends AppCompatActivity {

    private String name, phone, youtubeId;
    private TextView mNamePlace;
    private Button mGoButton;
    private Double mLongitude, mLatitude;
    private ImageView mImageView1, mImageView2, mImageView3, mImageView4;
    private ImageButton mPhoneDialer, mChat, mYoutubePlayer;

    private DatabaseReference mPlaceDatabase;

    private String downloadUrl, thumb_downloadUrl, downloadUrl2, thumb_downloadUrl2, downloadUrl3, thumb_downloadUrl3, downloadUrl4, thumb_downloadUrl4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final String placeUid = getIntent().getStringExtra("placeUid");

        mNamePlace = (TextView) findViewById(R.id.detail_text_name);
        mGoButton = (Button) findViewById(R.id.detail_button_go);
        mImageView1 = (ImageView) findViewById(R.id.detail_image1);
        mImageView2 = (ImageView) findViewById(R.id.detail_image2);
        mImageView3 = (ImageView) findViewById(R.id.detail_image3);
        mImageView4 = (ImageView) findViewById(R.id.detail_image4);
        mPhoneDialer = (ImageButton) findViewById(R.id.detail_imgbutton_phone);
        mChat = (ImageButton) findViewById(R.id.detail_imgbutton_chat);
        mYoutubePlayer = (ImageButton) findViewById(R.id.detail_image_playvid);



        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(placeUid);


        mPlaceDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    name = dataSnapshot.child("name").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    phone = dataSnapshot.child("phone").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String youtube = dataSnapshot.child("youtube").getValue().toString();
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

                    youtubeId = youtube;

                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    getSupportActionBar().setTitle(name);

                    mLongitude = Double.parseDouble(longitude);
                    mLatitude = Double.parseDouble(latitude);

                    mNamePlace.setText(name);

                    mImageView1.setScaleType(FIT_XY);
                    Picasso.with(DetailActivity.this).load(thumb_downloadUrl).into(mImageView1);

                    mImageView2.setScaleType(FIT_XY);
                    Picasso.with(DetailActivity.this).load(thumb_downloadUrl2).into(mImageView2);

                    mImageView3.setScaleType(FIT_XY);
                    Picasso.with(DetailActivity.this).load(thumb_downloadUrl3).into(mImageView3);

                    mImageView4.setScaleType(FIT_XY);
                    Picasso.with(DetailActivity.this).load(thumb_downloadUrl4).into(mImageView4);

                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "geo:0,0?q=" +mLatitude+ "," +mLongitude + "("  +name+  ")";

                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse(url));
                startActivity(mapIntent);

            }
        });

        mPhoneDialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);

            }
        });

        mYoutubePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent youtubeIntent = new Intent(DetailActivity.this, YoutubeActivity.class);
                youtubeIntent.putExtra("youtube", youtubeId);
                startActivity(youtubeIntent);


            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();



    }
}
