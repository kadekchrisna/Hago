package com.kadek.tripgo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPlacesActivity extends AppCompatActivity {

    private Button mDeleteButton;
    private DatabaseReference mPlaceDatabase, mUserPlaceDatabase;
    private FirebaseUser mCurrentUser;
    private String currentUid;
    private TextView mtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_places2);

        String placeUid = getIntent().getStringExtra("user_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = mCurrentUser.getUid();
        mtext=(TextView)findViewById(R.id.textView5);
        mtext.setText(placeUid);

        mDeleteButton = (Button) findViewById(R.id.edit_button_delete);

        mPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Places").child(placeUid);
        mUserPlaceDatabase = FirebaseDatabase.getInstance().getReference().child("Owner").child(currentUid).child(placeUid);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserPlaceDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            mPlaceDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Intent placeIntent = new Intent(EditPlacesActivity.this, MainActivity.class);
                                        startActivity(placeIntent);
                                        finish();

                                    }else {
                                        Toast.makeText(EditPlacesActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                });

            }
        });

    }
}
