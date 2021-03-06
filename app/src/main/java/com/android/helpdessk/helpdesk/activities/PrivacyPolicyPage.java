package com.android.helpdessk.helpdesk.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyPolicyPage extends AppCompatActivity {

    private RequestQueue privacyQueue;
    private TextView privacyPolicyHtml;
    private Button acceptButton, notAcceptButton;
    private LinearLayout btnAcceptDeclineContainer;
    private RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_page);

        loadingPanel = findViewById(R.id.loadingPanel);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        String fullname = sharedPreferences.getString("fullname","");
        String email = sharedPreferences.getString("email","");
        String phone = sharedPreferences.getString("phone","");

//        Toast.makeText(PrivacyPolicyPage.this, user+" "+fullname+" "+email+" "+phone , Toast.LENGTH_LONG).show();

        privacyPolicyHtml = findViewById(R.id.privacy_policy_html);
        privayPolicyContentWordpress();

        acceptButton = findViewById(R.id.accept_button);
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

        notAcceptButton = findViewById(R.id.not_accept);
        notAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PrivacyPolicyPage.this,"Please Read and Click I Accept",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void privayPolicyContentWordpress(){

        //Toast.makeText(PrivacyPolicyPage.this,"Privacy policy call",Toast.LENGTH_SHORT).show();
        // Get data for privacy policy

        // Instantiate the RequestQueue.
        privacyQueue = Volley.newRequestQueue(PrivacyPolicyPage.this);
        String getPrivacyPolicyURL ="https://helpdessk.com/wp-json/wp/v2/pages/4562";

        // prepare the Request
        JsonObjectRequest getPrivacyContent = new JsonObjectRequest(Request.Method.GET, getPrivacyPolicyURL, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{
                            JSONObject privacyContentParent = response.getJSONObject("content");
                            String privacyHtml = privacyContentParent.getString("rendered");
                            privacyPolicyHtml.setText(Html.fromHtml(privacyHtml));
                            privacyPolicyHtml.setMovementMethod(new ScrollingMovementMethod());
                            btnAcceptDeclineContainer = findViewById(R.id.btnAcceptDeclineContainer);
                            btnAcceptDeclineContainer.setVisibility(View.VISIBLE);
                            loadingPanel.setVisibility(View.GONE);
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TO DO Add default content
                        Log.d("WordPr Error.Response", error.toString());
                        //acceptButton.setEnabled(true);
                        //notAcceptButton.setEnabled(true);
                    }
                }
        );

        // add it to the RequestQueue
        privacyQueue.add(getPrivacyContent);

        // Register Data in Wordpress
    }
}
