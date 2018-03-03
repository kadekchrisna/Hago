package com.kadek.tripgo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class PlaceActivity extends AppCompatActivity {

    private FloatingActionButton mAddPlaceFloatButton;
    private DatabaseReference mUserPlaceDatabase, mPlaceDatabase;
    private RecyclerView mUserPlaceList;
    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mAddPlaceFloatButton = (FloatingActionButton) findViewById(R.id.addplace_button);
        mToolbar = (Toolbar) findViewById(R.id.add_app_bar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maintain");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();
        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid);
        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places");


        mUserPlaceList = (RecyclerView) findViewById(R.id.user_place_list);
        mUserPlaceList.setHasFixedSize(true);
        mUserPlaceList.setLayoutManager(new LinearLayoutManager(this));


        mAddPlaceFloatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editIntent = new Intent(PlaceActivity.this, AddPlacesActivity.class);
                startActivity(editIntent);

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid);

        FirebaseRecyclerAdapter<Owner, OwnerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Owner, OwnerViewHolder>(

                Owner.class,
                R.layout.place_single_layout,
                OwnerViewHolder.class,
                mUserPlaceDatabase



        ) {
            @Override
            protected void populateViewHolder(final OwnerViewHolder viewHolder, Owner model, final int position) {

                final String user_id = getRef(position).getKey();
                mPlaceDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            String name = dataSnapshot.child("name").getValue().toString();
                            String price = dataSnapshot.child("price").getValue().toString();
                            String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();


                            viewHolder.setName(name);
                            viewHolder.setPrice(price);
                            viewHolder.setThumb_image(thumb_image, getApplicationContext());
                        }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                CharSequence option[] = new CharSequence[]{"Edit", "Delete"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                                builder.setTitle("Select Options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0){

                                            Intent editIntent = new Intent(PlaceActivity.this, EditPlacesActivity.class);
                                            editIntent.putExtra("user_id", user_id);
                                            startActivity(editIntent);

                                        }
                                        if (i == 1){

                                            mUserPlaceDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        mPlaceDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    Toast.makeText(PlaceActivity.this, "Success Deleteing", Toast.LENGTH_SHORT).show();

                                                                }else {
                                                                    Toast.makeText(PlaceActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
        mUserPlaceList.setAdapter(firebaseRecyclerAdapter);



    }


    public static class OwnerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public OwnerViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name){

            TextView mPlaceName = (TextView) mView.findViewById(R.id.place_name);
            mPlaceName.setText(name);

        }

        public void setPrice(String price) {
            TextView mPlaceStatus = (TextView) mView.findViewById(R.id.place_price);
            mPlaceStatus.setText(price);
        }


        public void setThumb_image(final String thumb_image, final Context ctx){

            final ImageView mPlaceImage = (ImageView) mView.findViewById(R.id.place_image);
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.palceholder).into(mPlaceImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.palceholder).into(mPlaceImage);

                }
            });

        }


    }
}
