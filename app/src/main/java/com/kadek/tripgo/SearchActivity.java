package com.kadek.tripgo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {

    private EditText mInputSearch;
    private ImageButton mButtonSearch;
    private String mSearch;

    private RecyclerView mListSearch;

    private DatabaseReference mSearchDatabse;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSearchDatabse = FirebaseDatabase.getInstance().getReference("Places");

        mInputSearch = (EditText) findViewById(R.id.inputSearch);
        mButtonSearch = (ImageButton) findViewById(R.id.butonSearch);

        mListSearch = (RecyclerView) findViewById(R.id.search_list);
        mListSearch.setHasFixedSize(true);
        mListSearch.setLayoutManager(new LinearLayoutManager(this));


        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mInputSearch.getText().toString();
                String uppperText = searchText.substring(0,1).toUpperCase() + searchText.substring(1);

                firebaseSearch(uppperText);

            }
        });


        mInputSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {

                    String searchText = mInputSearch.getText().toString();
                    String uppperText = searchText.substring(0,1).toUpperCase() + searchText.substring(1);
                    firebaseSearch(uppperText);
                    return true;
                }
                return false;
            }
        });
    }


    private void firebaseSearch(final String searchText) {

        Query firebaseQuery = mSearchDatabse.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");


        FirebaseRecyclerAdapter<Search, SearchViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search, SearchViewHolder>(


                Search.class,
                R.layout.place_single_layout,
                SearchViewHolder.class,
                firebaseQuery


        ) {
            @Override
            protected void populateViewHolder(SearchViewHolder viewHolder, Search model, int position) {


                viewHolder.setDetails(getApplicationContext(), model.getName(), model.getDescription(), model.getImage());

                final String placeUid = getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detailIntent = new Intent(SearchActivity.this, DetailActivity.class);
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


    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public SearchViewHolder(View itemView) {

            super(itemView);

            mView = itemView;

        }


        public void setDetails(final Context ctx, String name, String desc, final String image){

            TextView nameP = (TextView) mView.findViewById(R.id.place_name);
            TextView descP = (TextView) mView.findViewById(R.id.place_price);
            final ImageView imageP = (ImageView) mView.findViewById(R.id.place_image);

            nameP.setText(name);
            descP.setText(desc);

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
