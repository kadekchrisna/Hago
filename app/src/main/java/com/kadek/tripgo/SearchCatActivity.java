package com.kadek.tripgo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class SearchCatActivity extends AppCompatActivity {

    private EditText mInputSearch;
    private ImageButton mButtonSearch;
    private String mSearch;

    private RecyclerView mListSearch;

    private DatabaseReference mSearchDatabse;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cat);

        final String type = getIntent().getStringExtra("type");

        switch (type){
            case "Buatan":
                mSearchDatabse = FirebaseDatabase.getInstance().getReference().child("Buatan");
                break;

            case "Alam":
                mSearchDatabse = FirebaseDatabase.getInstance().getReference().child("Alam");
                break;

            case "Bahari":
                mSearchDatabse = FirebaseDatabase.getInstance().getReference().child("Bahari");
                break;

            case "Budaya":
                mSearchDatabse = FirebaseDatabase.getInstance().getReference().child("Budaya");
                break;
        }

        mInputSearch = (EditText) findViewById(R.id.inputSearchCat);
        mButtonSearch = (ImageButton) findViewById(R.id.buttonSearchCat);
        mListSearch = (RecyclerView) findViewById(R.id.search_cat_list);
        mListSearch.setHasFixedSize(true);
        mListSearch.setLayoutManager(new LinearLayoutManager(this));



        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInputSearch.getText().toString().isEmpty()){

                    Toast.makeText(SearchCatActivity.this, "Please insert text first", Toast.LENGTH_SHORT).show();

                }else {
                    String searchText = mInputSearch.getText().toString();
                    String uppperText = searchText.substring(0,1).toUpperCase() + searchText.substring(1);

                    firebaseSearch(uppperText);

                }



            }
        });

        mInputSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    if (mInputSearch.getText().toString().isEmpty()){

                        Toast.makeText(SearchCatActivity.this, "Please insert text first", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    }else {

                        String searchText = mInputSearch.getText().toString();
                        String uppperText = searchText.substring(0,1).toUpperCase() + searchText.substring(1);
                        firebaseSearch(uppperText);

                        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        return true;

                    }

                }
                return false;
            }
        });

    }


    private void firebaseSearch(final String searchText) {

        final Query firebaseQuery = mSearchDatabse.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");


        FirebaseRecyclerAdapter<Search, SearchCatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search, SearchCatViewHolder>(


                Search.class,
                R.layout.place_single_layout,
                SearchCatViewHolder.class,
                firebaseQuery


        ) {
            @Override
            protected void populateViewHolder(final SearchCatViewHolder viewHolder, final Search model, final int position) {

                viewHolder.setDetails(getApplicationContext(), model.getName(), model.getPrice(), model.getImage());


                final String placeUid = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detailIntent = new Intent(SearchCatActivity.this, DetailActivity.class);
                        detailIntent.putExtra("placeUid", placeUid);
                        startActivity(detailIntent);
                        detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                    }
                });
            }
        };
        mListSearch.setAdapter(firebaseRecyclerAdapter);

    }

    public static class SearchCatViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public SearchCatViewHolder(View itemView) {

            super(itemView);

            mView = itemView;

        }


        public void setDetails(final Context ctx, String name, String price, final String image){

            TextView nameP = (TextView) mView.findViewById(R.id.place_name);
            TextView descP = (TextView) mView.findViewById(R.id.place_price);
            final ImageView imageP = (ImageView) mView.findViewById(R.id.place_image);


            int mPrice = Integer.parseInt(price);
            price = NumberFormat.getNumberInstance(Locale.US).format(mPrice);

            nameP.setText(name);
            descP.setText(ctx.getString(R.string.textPrice)+price);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.placeholder).into(imageP, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).placeholder(R.drawable.placeholder).into(imageP);

                }
            });



        }
    }

}

