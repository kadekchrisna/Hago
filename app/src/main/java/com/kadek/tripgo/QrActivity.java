package com.kadek.tripgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QrActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;
    private DatabaseReference mSearchDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.setAutoFocus(true);
        zXingScannerView.startCamera();

        mSearchDatabase = FirebaseDatabase.getInstance().getReference().child("Places");



    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(final Result result) {
        Toast.makeText(this, ""+result, Toast.LENGTH_SHORT).show();

        final String id = String.valueOf(result);

        mSearchDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id)){

                    Intent detailIntent = new Intent(QrActivity.this, DetailActivity.class);
                    detailIntent.putExtra("placeUid", id);
                    startActivity(detailIntent);
                    detailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();

                }else{
                    Toast.makeText(QrActivity.this, "Tempat pariwisata tidak di temukan. ", Toast.LENGTH_SHORT).show();
                    zXingScannerView.resumeCameraPreview(QrActivity.this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //Toast.makeText(getApplicationContext(),result.getText(), Toast.LENGTH_SHORT).show();
        //zXingScannerView.resumeCameraPreview(this);

    }
}
