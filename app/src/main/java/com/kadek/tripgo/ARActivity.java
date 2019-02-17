package com.kadek.tripgo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class ARActivity extends AppCompatActivity {

    WebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        mWeb = (WebView) findViewById(R.id.ARwebview);
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.getSettings().setDomStorageEnabled(true);
        mWeb.loadUrl("https://ar.pahawangtravel.id/");


    }
}
