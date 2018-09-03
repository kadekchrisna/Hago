package com.kadek.tripgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EventDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mMainImage;
    private TextView mNameEvent, mDescEvent, mEventStartText, mEventEndText;
    private ImageButton mGo;

    private DatabaseReference mEventDatabase;

    private String mId, mName, mDesc, mEventEnd, mEventStart, mEventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mToolbar = (Toolbar) findViewById(R.id.event_detail_app_bar);
        mNameEvent = (TextView) findViewById(R.id.event_detail_name);
        mMainImage = (ImageView) findViewById(R.id.event_detail_image);
        mDescEvent = (TextView) findViewById(R.id.event_detail_description);
        mEventStartText = (TextView) findViewById(R.id.event_detail_mulai);
        mEventEndText = (TextView) findViewById(R.id.event_detail_akhir);
        mGo = (ImageButton) findViewById(R.id.event_button_go);



        mId = getIntent().getStringExtra("event_id");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event").child(mId);
        mEventDatabase.keepSynced(true);


        mEventDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()){

                    mName = dataSnapshot.child("name").getValue().toString();
                    mDesc = dataSnapshot.child("description").getValue().toString();
                    mEventEnd = dataSnapshot.child("event_end").getValue().toString();
                    mEventStart = dataSnapshot.child("event_start").getValue().toString();
                    mEventImage = dataSnapshot.child("event_image").getValue().toString();


                    if (dataSnapshot.hasChild("idplace")){

                        mGo.setVisibility(View.VISIBLE);
                        final String placeUid = dataSnapshot.child("idplace").getValue().toString();

                        mGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent detailIntent = new Intent(EventDetailActivity.this, DetailActivity.class);
                                detailIntent.putExtra("placeUid", placeUid);
                                startActivity(detailIntent);
                                detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                            }
                        });

                    }


                    getSupportActionBar().setTitle(mName);



                    mNameEvent.setText(mName);
                    mDescEvent.setText(mDesc);
                    mEventStartText.setText(mEventStart);
                    mEventEndText.setText(mEventEnd);

                    Picasso.with(EventDetailActivity.this).load(mEventImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mMainImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(EventDetailActivity.this).load(mEventImage).placeholder(R.drawable.placeholder).into(mMainImage);

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }
}
