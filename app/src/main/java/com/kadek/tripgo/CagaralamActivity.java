package com.kadek.tripgo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class CagaralamActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mCagaralamList;


    private DatabaseReference mCagaralamDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cagaralam);

        mToolbar = (Toolbar) findViewById(R.id.cagaralam_app_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Alam");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCagaralamDatabase = FirebaseDatabase.getInstance().getReference().child("Alam");
        mCagaralamDatabase.keepSynced(true);

        mCagaralamList = (RecyclerView) findViewById(R.id.cagaralam_list);
        mCagaralamList.setHasFixedSize(true);
        mCagaralamList.setLayoutManager(new LinearLayoutManager(this));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.search_toolbar:
                Intent detailIntent = new Intent(CagaralamActivity.this, SearchCatActivity.class);
                detailIntent.putExtra("type", "Alam");
                startActivity(detailIntent);
                detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Places, BahariActivity.PlacesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Places, BahariActivity.PlacesViewHolder>(

                Places.class,
                R.layout.place_single_layout,
                BahariActivity.PlacesViewHolder.class,
                mCagaralamDatabase.orderByChild("name")

        ) {
            @Override
            protected void populateViewHolder(BahariActivity.PlacesViewHolder viewHolder, Places model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setThumb_image(model.getThumb_image(), getApplicationContext());

                final String placeUid = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detailIntent = new Intent(CagaralamActivity.this, DetailActivity.class);
                        detailIntent.putExtra("placeUid", placeUid);
                        startActivity(detailIntent);
                        detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();

                    }
                });

            }
        };

        mCagaralamList.setAdapter(firebaseRecyclerAdapter);

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

            int mPrice = Integer.parseInt(price);
            price = NumberFormat.getNumberInstance(Locale.US).format(mPrice);
            mPlaceStatus.setText("Mulai dari Rp"+price);
        }


        public void setThumb_image(final String thumb_image, final Context ctx){

            final ImageView mPlaceImage = (ImageView) mView.findViewById(R.id.place_image);
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

        Intent backIntent = new Intent(CagaralamActivity.this, MainActivity.class);
        startActivity(backIntent);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
