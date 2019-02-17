package com.kadek.tripgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductAddActivity extends AppCompatActivity {

    private TextView mProductName;
    private Toolbar mToolbar;
    private TextInputLayout mLink;
    private Spinner mDropdown;
    private Button mSave;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mProductDatabase, mUserPlaceDatabase, mPlaceDatabase, mProductOwnDatabase, mUserProductDatabase;
    private FirebaseUser mCurrentUser;
    private String currentUid, mCategory, mNamePlace, mIdPlace, mTrueLink, mCase;

    private List<String> list = new ArrayList<String>();
    private Map map = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        list.add(0, "Pilih Tempat Wisata");

        mToolbar = (Toolbar) findViewById(R.id.product_add_app_bar);
        mProductName = (TextView) findViewById(R.id.product_title_input);
        mLink = (TextInputLayout) findViewById(R.id.product_link_input);
        mSave = (Button) findViewById(R.id.product_save_button);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tambah Souvenir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();


        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places");
        mProductDatabase = FirebaseDatabase.getInstance().getReference().child("Product");
        mProductOwnDatabase = FirebaseDatabase.getInstance().getReference().child("ProductOwn");
        mUserProductDatabase = FirebaseDatabase.getInstance().getReference();

        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid);


        mUserPlaceDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final int place_id = (int) dataSnapshot.getChildrenCount();

                for (DataSnapshot child : dataSnapshot.getChildren()){

                    mPlaceDatabase.child(child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.exists()){

                                String name = dataSnapshot.child("name").getValue().toString();
                                String mPlace = dataSnapshot.getKey().toString();
                                list.add(name);
                                map.put(name, mPlace);



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






    }

    @Override
    protected void onStart() {
        super.onStart();

        mDropdown = (Spinner) findViewById(R.id.product_spinner_input);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProductAddActivity.this,
                android.R.layout.simple_spinner_dropdown_item, list);
        dataAdapter.notifyDataSetChanged();
        mDropdown.setAdapter(dataAdapter);
        mDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mCategory = adapterView.getItemAtPosition(i).toString();
                mNamePlace = mCategory;
                if (map.containsKey(mCategory)){

                    Object key = map.get(mCategory);
                    mIdPlace = String.valueOf(key);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });







        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mLink.getEditText().getText().toString().isEmpty() || mCategory.equals("Pilih Tempat Wisata")){
                    Toast.makeText(ProductAddActivity.this, "Please check the form and then click save again", Toast.LENGTH_SHORT).show();
                }else {
                    mProgressDialog = new ProgressDialog(ProductAddActivity.this);
                    mProgressDialog.setTitle("Saving Changes");
                    mProgressDialog.setMessage("Please Wait...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    mTrueLink = mLink.getEditText().getText().toString();
                    if (URLUtil.isHttpsUrl(mTrueLink)){
                        mCase = mTrueLink.substring(mTrueLink.indexOf(".")+1,mTrueLink.lastIndexOf(".com"));


                        if (mCase.equals("bukalapak")){

                            mTrueLink = mTrueLink.replace("https://m.", "https://www.");
                            //new linkParser().execute(mTrueLink);

                        }else if (mCase.equals("tokopedia")){

                            mTrueLink = mTrueLink.replace("https://m.", "https://www.");
                            //new linkParser().execute(mTrueLink);

                        }else{
                            mProgressDialog.dismiss();
                            Toast.makeText(ProductAddActivity.this, "Masukan produk hanya dari Tokopedia atau Bukalapak", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        mProgressDialog.dismiss();
                        Toast.makeText(ProductAddActivity.this, "Masukan produk hanya dari Tokopedia atau Bukalapak", Toast.LENGTH_SHORT).show();
                    }



                }

            }
        });




    }

    public class linkParser extends AsyncTask<String, Void, String> {

        private String pName, pPrice, pLink;


        @Override
        protected String doInBackground(String... strings) {
            Document document;

            try {

                if (mCase.equals("tokopedia")){

                    document = Jsoup.connect(mTrueLink).get();
                    Elements newsHeadlines = document.select(".content-main-img");
                    Elements mPName = document.select("#product-name");
                    Elements mPPrice = document.select("#product_price_int");
                    pName = mPName.attr("value").toString();
                    pPrice = mPPrice.attr("value").toString();
                    Elements mPImage = newsHeadlines.select("img");
                    pLink = mPImage.attr("src").toString();


                }else if (mCase.equals("bukalapak")){

                    document = Jsoup.connect(mTrueLink).get();
                    Elements newsHeadlines = document.select(".c-product-image-gallery__image");
                    Elements mPName = document.select(".c-product-detail__name");
                    Elements mPPrice = document.select(".c-product-detail-price");
                    pName = mPName.tagName("h1").text().toString();
                    pPrice = mPPrice.attr("data-reduced-price").toString();
                    pLink = newsHeadlines.attr("href").toString();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            final Map linkMap = new HashMap();
            if (pName.isEmpty() || pPrice.isEmpty() || pLink.isEmpty()){
                Toast.makeText(ProductAddActivity.this, "Masukan link produk dengan lengkap", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }else{

                linkMap.put("name", pName);
                linkMap.put("place", mNamePlace);
                linkMap.put("price", pPrice);
                linkMap.put("image", pLink);
                linkMap.put("link", mTrueLink);
                linkMap.put("idplace", mIdPlace);
                final String mProductKey = mProductDatabase.push().getKey();
                final Map prodMap = new HashMap();
                prodMap.put("product",mProductKey);

                mProductOwnDatabase.child(mIdPlace).child(mProductKey).setValue(prodMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            mUserProductDatabase.child("ProductUser").child(currentUid).child(mProductKey).setValue(prodMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        mProductDatabase.child(mProductKey).setValue(linkMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Intent backIntent = new Intent(ProductAddActivity.this, ProductActivity.class);
                                                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(backIntent);
                                                    finish();

                                                }else {


                                                    Toast.makeText(ProductAddActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                                    mProgressDialog.dismiss();


                                                }

                                            }
                                        });


                                    }else {

                                        Toast.makeText(ProductAddActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                                        mProgressDialog.dismiss();

                                    }

                                }
                            });

                        }else {

                            Toast.makeText(ProductAddActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }

                    }
                });
            }




        }
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(ProductAddActivity.this, AdminActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
        finish();
    }
}

