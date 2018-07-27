package com.android.helpdessk.helpdesk.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;
import com.android.helpdessk.helpdesk.activities.PrivacyPolicyPage;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyPolicyFragment extends Fragment {

    private TextView privacyPolicyHtml;
    private RequestQueue privacyQueue;

    public static PrivacyPolicyFragment newInstance(){
        return new PrivacyPolicyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.privacy_page_fragment, container, false);
        privacyPolicyHtml = view.findViewById(R.id.privacy_policy_html);

        privayPolicyContentWordpress();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void privayPolicyContentWordpress(){

        Toast.makeText(getActivity(),"Privacy policy call",Toast.LENGTH_SHORT).show();
        // Get data for privacy policy

        // Instantiate the RequestQueue.
        privacyQueue = Volley.newRequestQueue(getActivity());
        String getprivacyPplicyurl ="https://helpdessk.com/wp-json/wp/v2/pages/4562";

        // prepare the Request
        JsonObjectRequest getPrivacyContent = new JsonObjectRequest(Request.Method.GET, getprivacyPplicyurl, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{
                            JSONObject privacycontentParent = response.getJSONObject("content");
                            String privacyHtml = privacycontentParent.getString("rendered");

                            privacyPolicyHtml.setText(Html.fromHtml(privacyHtml));
                            privacyPolicyHtml.setMovementMethod(new ScrollingMovementMethod());
                            //acceptButton.setEnabled(true);
                            //notAcceptButton.setEnabled(true);

                            Log.d("WordPr Success Response", privacyHtml);
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
