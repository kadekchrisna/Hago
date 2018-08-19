package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
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
    private DatabaseReference mProductDatabase, mUserProductDatabase, mProductOwnDatabase ;
    private RecyclerView mUserProductList;

    private FirebaseUser mCurrentUser;
    private String currentUid;
    private String mPrice, mLink, mName, mPlace, mIdPlace;
    private String productUid = FirebaseDatabase.getInstance().getReference().child("Product").push().getKey();

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
        getSupportActionBar().setTitle("Kelola Souvenir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProductDatabase = FirebaseDatabase.getInstance().getReference().child("Product");
        mProductOwnDatabase = FirebaseDatabase.getInstance().getReference().child("ProductOwn");



        mUserProductList = (RecyclerView) findViewById(R.id.product_list);
        mUserProductList.setHasFixedSize(true);
        mUserProductList.setLayoutManager(new LinearLayoutManager(this));

        mText = (TextView) findViewById(R.id.event_litle_description);
        mIview = (ImageView) findViewById(R.id.event_image_preview);


    }

    @Override
    protected void onStart() {
        super.onStart();

        mUserProductDatabase = FirebaseDatabase.getInstance().getReference().child("ProductUser").child(currentUid);

        FirebaseRecyclerAdapter<Product, LinkViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, LinkViewHolder>(

                Product.class,
                R.layout.product_single_owner,
                LinkViewHolder.class,
                mUserProductDatabase

        ) {
            @Override
            protected void populateViewHolder(final LinkViewHolder viewHolder, Product model, int position) {

                final String place_id = getRef(position).getKey();
                mProductDatabase.child(place_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){


                            mLink = dataSnapshot.child("image").getValue().toString();
                            mPlace = dataSnapshot.child("place").getValue().toString();
                            mName = dataSnapshot.child("name").getValue().toString();
                            mPrice = dataSnapshot.child("price").getValue().toString();
                            mIdPlace = dataSnapshot.child("idplace").getValue().toString();


                            viewHolder.setImage(mLink, getApplicationContext());
                            viewHolder.setPlace(mPlace);
                            viewHolder.setName(mName);
                            viewHolder.setPrice(mPrice);

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

                                                mProgressDialog = new ProgressDialog(ProductActivity.this);
                                                mProgressDialog.setTitle("Deleting Changes");
                                                mProgressDialog.setMessage("Please Wait...");
                                                mProgressDialog.show();

                                                mUserProductDatabase.child(place_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){

                                                            mProductOwnDatabase.child(mIdPlace).child(place_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        mProductDatabase.child(place_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()){

                                                                                    mProgressDialog.dismiss();


                                                                                }

                                                                            }
                                                                        });

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



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mUserProductList.setAdapter(firebaseRecyclerAdapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addProductIntent = new Intent(ProductActivity.this, ProductAddActivity.class);
                startActivity(addProductIntent);
            }
        });




    }

    public static class LinkViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public LinkViewHolder (View itemView){
            super(itemView);

            mView = itemView;


        }
        public void setName(String name) {
            TextView mLink = (TextView) mView.findViewById(R.id.product_owner_name);
            mLink.setText(name);
        }

        public void setPlace(String place){
            TextView mPlace = (TextView) mView.findViewById(R.id.product_owner_place);
            mPlace.setText(place);
        }

        public void setPrice(String price){
            TextView mPlace = (TextView) mView.findViewById(R.id.product_owner_price);
            mPlace.setText(price);
        }



        public void setImage(final String image, final Context ctx){

            final ImageView mPlaceImage = (ImageView) mView.findViewById(R.id.product_owner_image);
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
