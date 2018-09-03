package com.kadek.tripgo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import static android.widget.ImageView.ScaleType.CENTER_INSIDE;
import static android.widget.ImageView.ScaleType.FIT_CENTER;
import static android.widget.ImageView.ScaleType.FIT_XY;

public class DetailActivity extends AppCompatActivity {

    private String name, phone, youtubeId, ownerId, userId, mLink, mPrice, mLinkImage, mName, mPlace;
    private TextView mNamePlace;
    private Button mGoButton;
    private Double mLongitude, mLatitude;
    private ImageView mImageView1, mImageView2, mImageView3, mImageView4, mImageVid;
    private ImageButton mPhoneDialer, mChat, mYoutubePlayer;
    private RecyclerView mProductRecView;
    private RelativeLayout mRelative;

    private FirebaseAuth mAuth;
    private View mView;

    private DatabaseReference mPlaceDatabase, mProductOwn, mProductList, mRootRef;

    private String downloadUrl, thumb_downloadUrl, downloadUrl2, thumb_downloadUrl2, downloadUrl3, thumb_downloadUrl3, downloadUrl4, thumb_downloadUrl4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        final String placeUid = getIntent().getStringExtra("placeUid");
        mPlace = placeUid;

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();


        mNamePlace = (TextView) findViewById(R.id.detail_text_name);
        mGoButton = (Button) findViewById(R.id.detail_button_go);
        mImageView1 = (ImageView) findViewById(R.id.detail_image1);
        mImageView2 = (ImageView) findViewById(R.id.detail_image2);
        mImageView3 = (ImageView) findViewById(R.id.detail_image3);
        mImageView4 = (ImageView) findViewById(R.id.detail_image4);
        mImageVid = (ImageView) findViewById(R.id.detail_image_vid_preview);
        mPhoneDialer = (ImageButton) findViewById(R.id.detail_imgbutton_phone);
        mChat = (ImageButton) findViewById(R.id.detail_imgbutton_chat);
        mYoutubePlayer = (ImageButton) findViewById(R.id.detail_image_playvid);


        mProductRecView = (RecyclerView) findViewById(R.id.product_list_detail);
        mProductRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mProductList = FirebaseDatabase.getInstance().getReference().child("Product");
        mProductOwn = FirebaseDatabase.getInstance().getReference().child("ProductOwn").child(placeUid);
        mProductList.keepSynced(true);
        mProductOwn.keepSynced(true);

        mRootRef = FirebaseDatabase.getInstance().getReference().child("Places");


        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(placeUid);
        mPlaceDatabase.keepSynced(true);

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mPlace)){

                    mPlaceDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {

                                name = dataSnapshot.child("name").getValue().toString();
                                String price = dataSnapshot.child("price").getValue().toString();
                                phone = dataSnapshot.child("phone").getValue().toString();
                                String description = dataSnapshot.child("description").getValue().toString();
                                String youtube = dataSnapshot.child("youtube").getValue().toString();
                                ownerId = dataSnapshot.child("owner").getValue().toString();
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

                                mImageVid.setScaleType(FIT_XY);
                                mImageVid.getAdjustViewBounds();
                                Picasso.with(DetailActivity.this).load("https://img.youtube.com/vi/"+ youtube +"/0.jpg").into(mImageVid);



                                if (ownerId.equals(userId)){

                                    mChat.setVisibility(View.INVISIBLE);
                                }else {

                                    mChat.setVisibility(View.VISIBLE);

                                }


                                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                                setSupportActionBar(toolbar);
                                getSupportActionBar().setTitle(name);

                                mLongitude = Double.parseDouble(longitude);
                                mLatitude = Double.parseDouble(latitude);

                                mNamePlace.setText(name);

                                mImageView1.setScaleType(FIT_XY);
                                Picasso.with(DetailActivity.this).load(thumb_downloadUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mImageView1, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(DetailActivity.this).load(thumb_downloadUrl).placeholder(R.drawable.placeholder).into(mImageView1);

                                    }
                                });

                                mImageView2.setScaleType(FIT_XY);
                                Picasso.with(DetailActivity.this).load(thumb_downloadUrl2).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mImageView2, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(DetailActivity.this).load(thumb_downloadUrl2).placeholder(R.drawable.placeholder).into(mImageView2);

                                    }
                                });

                                mImageView3.setScaleType(FIT_XY);
                                Picasso.with(DetailActivity.this).load(thumb_downloadUrl3).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mImageView3, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(DetailActivity.this).load(thumb_downloadUrl3).placeholder(R.drawable.placeholder).into(mImageView3);

                                    }
                                });

                                mImageView4.setScaleType(FIT_XY);
                                Picasso.with(DetailActivity.this).load(thumb_downloadUrl4).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mImageView4, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(DetailActivity.this).load(thumb_downloadUrl4).placeholder(R.drawable.placeholder).into(mImageView4);

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
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(DetailActivity.this, ChatActivity.class);
                chatIntent.putExtra("user_name", name);
                chatIntent.putExtra("user_id", ownerId);
                startActivity(chatIntent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(

                Product.class,
                R.layout.pruduct_single,
                ProductViewHolder.class,
                mProductOwn


        ) {
            @Override
            protected void populateViewHolder(final ProductViewHolder viewHolder, Product model, int position) {
                final String place_id = getRef(position).getKey();

                mProductList.child(place_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            mLinkImage = dataSnapshot.child("image").getValue().toString();
                            mName = dataSnapshot.child("name").getValue().toString();
                            mPrice = dataSnapshot.child("price").getValue().toString();
                            mLink = dataSnapshot.child("link").getValue().toString();

                            int price = Integer.parseInt(mPrice);
                            mPrice = NumberFormat.getNumberInstance(Locale.US).format(price);


                            viewHolder.setImage(mLinkImage, getApplicationContext());
                            viewHolder.setName(mName);
                            viewHolder.setPrice("Rp"+mPrice);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                    intent.setData(Uri.parse(mLink));
                                    startActivity(intent);


                                }
                            });

                        }else {

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        };


        mProductRecView.setAdapter(firebaseRecyclerAdapter);
    }



    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView mLink = (TextView) mView.findViewById(R.id.product_name_detail);
            mLink.setText(name);
        }


        public void setPrice(String price){
            TextView mPlace = (TextView) mView.findViewById(R.id.product_price_detail);
            mPlace.setText(price);
        }



        public void setImage(final String image, final Context ctx){

            final ImageView mPlaceImage = (ImageView) mView.findViewById(R.id.product_image_detail);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(mPlaceImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).placeholder(R.drawable.placeholder).into(mPlaceImage);

                }
            });

        }
    }

}
