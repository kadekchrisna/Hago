package com.kadek.tripgo;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private Button mLogoutButton, mPlaceButton, mConvButton, mEventButton;
    private View mMainView;

    private GoogleApiClient mGoogleClient;
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
        mConvButton = (Button) mMainView.findViewById(R.id.account_conv_button);
        mEventButton = (Button) mMainView.findViewById(R.id.account_event_button);

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
                // Firebase sign out
                if (Auth.GoogleSignInApi != null) {
                    // Google sign out
                    mGoogleClient = new GoogleApiClient.Builder(getContext())
                            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(@Nullable Bundle bundle) {
                                    //SIGN OUT HERE
                                    Auth.GoogleSignInApi.signOut(mGoogleClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                    Auth.GoogleSignInApi.revokeAccess(mGoogleClient).setResultCallback(
                                                            new ResultCallback<Status>() {
                                                                @Override
                                                                public void onResult(@NonNull Status status) {

                                                                    sendToStart();
                                                                    mAuth.signOut();

                                                                }
                                                            });
                                                }
                                            });
                                }

                                @Override
                                public void onConnectionSuspended(int i) {/*ignored*/}
                            })
                            .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                                @Override
                                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            /*ignored*/
                                }
                            })
                            .addApi(Auth.GOOGLE_SIGN_IN_API) //IMPORTANT!!!
                            .build();

                    mGoogleClient.connect();

                }
            }
        });

        mConvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent convIntent = new Intent(getContext(), ConversationActivity.class);
                startActivity(convIntent);

            }
        });

        mEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent eventIntent = new Intent(getContext(), EventActivity.class);
                startActivity(eventIntent);
                
            }
        });

        return mMainView;

    }

    private void sendToStart() {

        Intent redirectIntent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(redirectIntent);

    }

}
