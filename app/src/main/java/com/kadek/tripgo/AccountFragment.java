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
public class AccountFragment extends Fragment {

    private Button mLogoutButton, mPlaceButton;
    private View mMainView;

    private FirebaseAuth mAuth;


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();

        mLogoutButton = (Button) mMainView.findViewById(R.id.account_logout_button);
        mPlaceButton = (Button) mMainView.findViewById(R.id.account_places);

        mPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent placesIntent = new Intent(getContext(), PlaceActivity.class);
                startActivity(placesIntent);

            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                sendToStart();


            }
        });

        return mMainView;

    }

    private void sendToStart() {

        Intent redirectIntent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(redirectIntent);
    }

}
