package com.android.helpdessk.helpdesk.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;

public class PrivacyPolicyPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_page);

        Button acceptButton = findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrivacyPolicyPage.this,"Accepted",Toast.LENGTH_SHORT).show();
            }
        });

        Button notAcceptButton = findViewById(R.id.not_accept);
        notAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrivacyPolicyPage.this,"Not Accepted",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
