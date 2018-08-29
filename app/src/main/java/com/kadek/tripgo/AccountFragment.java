package com.kadek.tripgo;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private ImageButton mLogoutButton, mConvButton, mAdminButton;
    private CircleImageView mProfile;
    private View mMainView;

    private DatabaseReference mUserDatabase;
    private GoogleApiClient mGoogleClient;
    private FirebaseAuth mAuth;


    String mCode = "1";

    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup containerAcc,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_account, containerAcc, false);

        mAuth = FirebaseAuth.getInstance();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLogoutButton = (ImageButton) mMainView.findViewById(R.id.account_logout_imagebutton);
        mConvButton = (ImageButton) mMainView.findViewById(R.id.account_conv_imagebutton);
        mAdminButton =(ImageButton) mMainView.findViewById(R.id.account_admin_imagebutton);
        mProfile = (CircleImageView) mMainView.findViewById(R.id.account_imageview);

        final FirebaseUser currentUser = mAuth.getCurrentUser();

        mUserDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String code = dataSnapshot.child("admin").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();

                    Picasso.with(getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar).into(mProfile, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(image).placeholder(R.drawable.placeholder).into(mProfile);

                        }
                    });

                    if (code.equals(mCode)){
                        mAdminButton.setVisibility(View.VISIBLE);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        mAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent adminIntent = new Intent(getContext(), AdminActivity.class);
                startActivity(adminIntent);
            }
        });


        return mMainView;

    }

    private void sendToStart() {

        Intent redirectIntent = new Intent(getContext(), WelcomeActivity.class);
        startActivity(redirectIntent);

    }

}
