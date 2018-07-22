package com.android.helpdessk.helpdesk.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;

public class LandingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


          SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
//        String user = sharedPreferences.getString("username","");
//        String fullname = sharedPreferences.getString("fullname","");
//        String email = sharedPreferences.getString("email","");
//        String phone = sharedPreferences.getString("phone","");
        Boolean loggedIn = sharedPreferences.getBoolean("loggedIn",false);

        if(loggedIn){
            Toast.makeText(LandingActivity.this, "Logged In" , Toast.LENGTH_LONG).show();
            //setContentView(R.layout.privacy_page);
            setContentView(R.layout.landing_screen);
            Button agreementButton = findViewById(R.id.agreement_button);
            agreementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(LandingActivity.this, PrivacyPolicyPage.class);
                    startActivity(i);
                }
            });
        }else{
            setContentView(R.layout.landing_screen);
            Button agreementButton = findViewById(R.id.agreement_button);
            agreementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(LandingActivity.this, PrivacyPolicyPage.class);
                    startActivity(i);
                }
            });
        }
    }
}
