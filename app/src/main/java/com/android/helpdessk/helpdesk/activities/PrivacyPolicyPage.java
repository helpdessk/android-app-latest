package com.android.helpdessk.helpdesk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;

public class PrivacyPolicyPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_page);
        //Get a reference to your WebView//
        WebView webView = (WebView) findViewById(R.id.privacy_webview);

        //Specify the URL you want to display//
        webView.loadUrl("http://helpdessk.com/");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        String fullname = sharedPreferences.getString("fullname","");
        String email = sharedPreferences.getString("email","");
        String phone = sharedPreferences.getString("phone","");

        Toast.makeText(PrivacyPolicyPage.this, user+" "+fullname+" "+email+" "+phone , Toast.LENGTH_LONG).show();


        Button acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("acceptAgreement",true);
                editor.apply();
                Intent i = new Intent(PrivacyPolicyPage.this,HomeScreenActivity.class);
                startActivity(i);
            }
        });

        Button notAcceptButton = findViewById(R.id.not_accept);
        notAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrivacyPolicyPage.this,"Please Read and Click I Accept",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
