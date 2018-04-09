package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {

    private FloatingActionButton mAddButton;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mLinkDatabase, mUserPlaceDatabase, mPlaceDatabase;
    private Spinner mDropdown;
    private RecyclerView mUserPlaceList;

    private FirebaseUser mCurrentUser;
    private String currentUid;
    private String mCategory, mIdPlace,mLink;

    private List<String> list = new ArrayList<String>();
    private Map map = new HashMap();

    private ImageView mIview;
    private TextView mText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        mAddButton = (FloatingActionButton) findViewById(R.id.product_add_button);
        mToolbar = (Toolbar) findViewById(R.id.product_app_bar);

        list.add(0, "Pilih Tempat Wisata");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tambah Souvenir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places");

        mUserPlaceList = (RecyclerView) findViewById(R.id.product_list);
        mUserPlaceList.setHasFixedSize(true);
        mUserPlaceList.setLayoutManager(new LinearLayoutManager(this));

        mText = (TextView) findViewById(R.id.event_litle_description);
        mIview = (ImageView) findViewById(R.id.event_image_preview);


    }

    @Override
    protected void onStart() {
        super.onStart();


        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid);

        FirebaseRecyclerAdapter<Owner, LinkViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Owner, LinkViewHolder>(

                Owner.class,
                R.layout.event_single_layout,
                LinkViewHolder.class,
                mUserPlaceDatabase

        ) {
            @Override
            protected void populateViewHolder(final LinkViewHolder viewHolder, final Owner model, int position) {

                final String place_id = getRef(position).getKey();
                mPlaceDatabase.child(place_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String name = dataSnapshot.child("name").getValue().toString();
                            String mPlace = dataSnapshot.getKey().toString();
                            list.add(name);
                            map.put(name, mPlace);

                            mLink = dataSnapshot.child("link").getValue().toString();

                            viewHolder.setPrice(mLink);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence option[] = new CharSequence[]{"Go", "Delete"};
                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductActivity.this);
                                    builder.setTitle("Select Options");
                                    builder.setItems(option, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if (i == 0){

                                                Intent intent = new Intent();
                                                intent.setAction(Intent.ACTION_VIEW);
                                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                                intent.setData(Uri.parse(mLink));
                                                startActivity(intent);

                                            }
                                            if (i == 1){

                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            });



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mUserPlaceList.setAdapter(firebaseRecyclerAdapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProductActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.add_product_popup, null);
                final TextInputLayout mLink = (TextInputLayout) mView.findViewById(R.id.popup_link);
                Button mSave = (Button) mView.findViewById(R.id.popup_save);
                mDropdown = (Spinner) mView.findViewById(R.id.popup_spinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProductActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, list);
                dataAdapter.notifyDataSetChanged();
                mDropdown.setAdapter(dataAdapter);
                mDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        mCategory = adapterView.getItemAtPosition(i).toString();
                        if (map.containsKey(mCategory)){

                            Object key = map.get(mCategory);
                            mIdPlace = String.valueOf(key);
                            Toast.makeText(ProductActivity.this, ""+key, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                mSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mLink.getEditText().getText().toString().isEmpty() || mCategory.equals("Pilih Tempat Wisata")){
                            Toast.makeText(ProductActivity.this, "Please check the form and then click save again", Toast.LENGTH_SHORT).show();
                        }else {

                            Map linkMap = new HashMap();
                            linkMap.put("link", mLink.getEditText().getText().toString());
                            mPlaceDatabase.child(mIdPlace).updateChildren(linkMap);
                            dialog.dismiss();
                            finish();
                            startActivity(getIntent());


                        }
                    }
                });

            }
        });


    }

    public static class LinkViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public LinkViewHolder (View itemView){
            super(itemView);

            mView = itemView;


        }
        public void setPrice(String price) {
            TextView mPlaceStatus = (TextView) mView.findViewById(R.id.event_single_name);
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
