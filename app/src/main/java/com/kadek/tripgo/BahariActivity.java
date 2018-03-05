package com.kadek.tripgo;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class BahariActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mBahariList;

    private DatabaseReference mBahariDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bahari);

        mToolbar = (Toolbar) findViewById(R.id.bahari_app_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bahari");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBahariDatabase = FirebaseDatabase.getInstance().getReference().child("Places");

        mBahariList = (RecyclerView) findViewById(R.id.bahari_list);
        mBahariList.setHasFixedSize(true);
        mBahariList.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Places, PlacesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Places, PlacesViewHolder>(

                Places.class,
                R.layout.place_single_layout,
                PlacesViewHolder.class,
                mBahariDatabase.orderByChild("category").equalTo("Bahari")

        ) {
            @Override
            protected void populateViewHolder(PlacesViewHolder viewHolder, Places model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setThumb_image(model.getThumb_image(), getApplicationContext());

                final String placeUid = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detailIntent = new Intent(BahariActivity.this, DetailActivity.class);
                        detailIntent.putExtra("placeUid", placeUid);
                        startActivity(detailIntent);

                    }
                });

            }
        };

        mBahariList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PlacesViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public PlacesViewHolder(View itemView) {
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
