package com.kadek.tripgo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class AgrowisataActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mAgrowisataList;
    private BottomNavigationView mAgroNav;

    private DatabaseReference mAgrowisataDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agrowisata);

        mToolbar = (Toolbar) findViewById(R.id.agrowisata_app_bar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Buatan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAgrowisataDatabase = FirebaseDatabase.getInstance().getReference().child("Buatan");
        mAgrowisataDatabase.keepSynced(true);

        mAgrowisataList = (RecyclerView) findViewById(R.id.agrowisata_list);
        mAgrowisataList.setHasFixedSize(true);
        mAgrowisataList.setLayoutManager(new LinearLayoutManager(this));



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
                Intent detailIntent = new Intent(AgrowisataActivity.this, SearchCatActivity.class);
                detailIntent.putExtra("type", "Buatan");
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


        FirebaseRecyclerAdapter<Places, AgrowisataActivity.PlacesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Places, AgrowisataActivity.PlacesViewHolder>(

                Places.class,
                R.layout.place_single_layout,
                AgrowisataActivity.PlacesViewHolder.class,
                mAgrowisataDatabase.orderByChild("name")

        ) {
            @Override
            protected void populateViewHolder(final AgrowisataActivity.PlacesViewHolder viewHolder, final Places model, final int position) {

                viewHolder.setName(model.getName());
                viewHolder.setPrice(model.getPrice());
                viewHolder.setThumb_image(model.getThumb_image(), getApplicationContext());

                final String placeUid = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detailIntent = new Intent(AgrowisataActivity.this, DetailActivity.class);
                        detailIntent.putExtra("placeUid", placeUid);
                        startActivity(detailIntent);
                        detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();

                    }
                });




            }
        };

        mAgrowisataList.setAdapter(firebaseRecyclerAdapter);



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

        Intent backIntent = new Intent(AgrowisataActivity.this, MainActivity.class);
        startActivity(backIntent);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
