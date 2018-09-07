package com.kadek.tripgo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Button mBahariButton, mQrButton, mAgrowisataButton, mCagaralamButton, mBudayaButton;
    private Button mSearchButton;
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
        mBudayaButton = (Button) mViewHome.findViewById(R.id.home_button_budaya);
        mAgrowisataButton = (Button) mViewHome.findViewById(R.id.home_button_agrowisata);
        mCagaralamButton = (Button) mViewHome.findViewById(R.id.home_button_cagaralam);
        mQrButton = (Button) mViewHome.findViewById(R.id.home_button_qr);
        mSearchButton = (Button) mViewHome.findViewById(R.id.home_button_search);

        mBahariButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(getContext(), BahariActivity.class);
                startActivity(backIntent);
            }
        });

        mBudayaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(getContext(), BudayaActivity.class);
                startActivity(backIntent);

            }
        });

        mAgrowisataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backIntent = new Intent(getContext(), AgrowisataActivity.class);
                startActivity(backIntent);
            }
        });

        mCagaralamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(getContext(), CagaralamActivity.class);
                startActivity(backIntent);
            }
        });

        mQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrIntent = new Intent(getContext(), QrActivity.class);
                startActivity(qrIntent);
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent searchIntent = new Intent(getContext(), SearchActivity.class);
                startActivity(searchIntent);
            }
        });






        return mViewHome;

    }

}
