package com.kadek.tripgo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class EventActivity extends AppCompatActivity {

    private FloatingActionButton mAddEventFloatButton;

    private DatabaseReference mEventDatabase, mEventOwnerDatabase;
    private ProgressDialog mProgressDialog;
    private StorageReference mImageStorage, mImageDelete;
    private RecyclerView mUserEventList;
    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;
    private String currentUid;
    private String downloadUrl, thumb_downloadUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        mToolbar = (Toolbar) findViewById(R.id.event_app_bar);

        mAddEventFloatButton = (FloatingActionButton) findViewById(R.id.addevent_button);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maintain Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();
        mEventOwnerDatabase = FirebaseDatabase.getInstance().getReference().child("EventOwner").child(currentUid);
        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");

        mUserEventList = (RecyclerView) findViewById(R.id.user_event_list);
        mUserEventList.setHasFixedSize(true);
        mUserEventList.setLayoutManager(new LinearLayoutManager(this));


        mAddEventFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addEventIntent = new Intent(EventActivity.this, EventAddActivity.class);
                startActivity(addEventIntent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        mEventOwnerDatabase = FirebaseDatabase.getInstance().getReference().child("EventOwner").child(currentUid);

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(

                Event.class,
                R.layout.event_single_layout,
                EventViewHolder.class,
                mEventOwnerDatabase



        ) {
            @Override
            protected void populateViewHolder(final EventViewHolder viewHolder, Event model, final int position) {

                final String event_id = getRef(position).getKey();
                mEventDatabase.child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String description = dataSnapshot.child("description").getValue().toString();
                            String thumb_image = dataSnapshot.child("event_thumb_image").getValue().toString();
                            String image = dataSnapshot.child("event_image").getValue().toString();


                            viewHolder.setName(name);
                            viewHolder.setDescription(description);
                            viewHolder.setThumb_image(thumb_image, getApplicationContext());
                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence option[] = new CharSequence[]{"Delete"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                                builder.setTitle("Select Options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, final int i) {

                                        if (i == 0){

                                            mProgressDialog = new ProgressDialog(EventActivity.this);
                                            mProgressDialog.setTitle("Deleting Changes");
                                            mProgressDialog.setMessage("Please Wait...");
                                            mProgressDialog.show();

                                            mEventOwnerDatabase.child(event_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        mEventDatabase.child(event_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    final String image = dataSnapshot.child("event_image").getValue().toString();
                                                                    final String thumb_image = dataSnapshot.child("event_thumb_image").getValue().toString();

                                                                    mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(image);
                                                                    mImageDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()){

                                                                                mImageDelete = FirebaseStorage.getInstance().getReferenceFromUrl(thumb_image);
                                                                                mImageDelete.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful()){

                                                                                            mProgressDialog.dismiss();

                                                                                        }

                                                                                    }
                                                                                });
                                                                            }else {
                                                                                Toast.makeText(EventActivity.this, "Please check internet connection and try again. ", Toast.LENGTH_SHORT).show();
                                                                                mProgressDialog.hide();
                                                                            }

                                                                        }
                                                                    });

                                                                    Toast.makeText(EventActivity.this, "Success Deleteing", Toast.LENGTH_SHORT).show();

                                                                }else {
                                                                    Toast.makeText(EventActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                }
                                            });

                                        }
                                    }
                                });
                                builder.show();

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        mUserEventList.setAdapter(firebaseRecyclerAdapter);



    }


    public static class EventViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public EventViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){

            TextView mPlaceName = (TextView) mView.findViewById(R.id.event_single_name);
            mPlaceName.setText(name);

        }

        public void setDescription(String description) {
            TextView mPlaceStatus = (TextView) mView.findViewById(R.id.event_litle_description);
            mPlaceStatus.setText(description.substring(0,10));
        }


        public void setThumb_image(final String thumb_image, final Context ctx){

            final ImageView mPlaceImage = (ImageView) mView.findViewById(R.id.event_image_preview);
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mPlaceImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.placeholder).into(mPlaceImage);

                }
            });

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(EventActivity.this, AdminActivity.class);
        startActivity(backIntent);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
