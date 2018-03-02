package com.kadek.tripgo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Button mBahariButton;
    private View mViewHome;

    private FirebaseAuth mAuth;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewHome = inflater.inflate(R.layout.fragment_home, container, false);

        mBahariButton = (Button) mViewHome.findViewById(R.id.home_button_bahari);

        mBahariButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(getContext(), BahariActivity.class);
                startActivity(backIntent);
            }
        });






        return mViewHome;

    }

}
