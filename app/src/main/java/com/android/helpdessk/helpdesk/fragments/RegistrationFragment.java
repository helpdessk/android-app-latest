package com.android.helpdessk.helpdesk.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.LaunchCallbacks;
import com.inscripts.interfaces.SubscribeCallbacks;

import org.json.JSONException;
import org.json.JSONObject;

import cometchat.inscripts.com.cometchatcore.coresdk.CometChat;

import static android.content.Context.MODE_PRIVATE;

public class RegistrationFragment extends Fragment {

    private static final String TAG = RegistrationFragment.class.getSimpleName();

    private CometChat cometChat;
    private String avatarURL = "";
    private String profileURL = "";
    private String role = "customer";
    private Context context;

    private Button btnLaunchChat;
    private EditText textUserName, textFullName, textEmail, textPhone;
    private ProgressBar pbLoading;

    private TextView mTextView;

    private RequestQueue queue;

    Fragment mCurrentFragment;
    
    public static RegistrationFragment newInstance(){
        return new RegistrationFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_layout, container, false);
        cometChat = CometChat.getInstance(getActivity());
        pbLoading = view.findViewById(R.id.pb_loading);
        btnLaunchChat = view.findViewById(R.id.btnRegisterAndLaunch);
        mTextView = (TextView) view.findViewById(R.id.wordpressResponse);

        btnLaunchChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAndLogin();
                // Created SharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username",textUserName.getText().toString());
                editor.putString("fullname",textFullName.getText().toString());
                editor.putString("email",textEmail.getText().toString());
                editor.putString("phone",textPhone.getText().toString());
                editor.putBoolean("loggedIn",true);
                editor.apply();
                // Created SharedPreferences
            }
        });
        textUserName = view.findViewById(R.id.user_name);
        textFullName = view.findViewById(R.id.user_full_name);
        textEmail = view.findViewById(R.id.user_email);
        textPhone = view.findViewById(R.id.user_phone);
        
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void createUserAndLogin(){
        cometChat.createUser(context,textUserName.getText().toString(),textFullName.getText().toString(),avatarURL,profileURL,role,new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.d(TAG, "Registration Successful : " + jsonObject.toString());
                String userResponseStatus;
                try{
                    if(jsonObject.toString().contains("success")){
                        userResponseStatus = jsonObject.getJSONObject("success").getString("status");
                        Log.d(TAG, "User Response : " + userResponseStatus);
                    }else{
                        userResponseStatus = jsonObject.getJSONObject("failed").getString("status");
                        Log.d(TAG, "User Response : " + userResponseStatus);
                    }


                    if(userResponseStatus.equals("2001")){
                        Toast.makeText(getActivity(), "User Already Exists or Failed User created", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "User Already Exists or Failed User created: " + userResponseStatus);
                    }else{ // success block after user created
                        Log.d(TAG, "User Success block " + userResponseStatus);
                        Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        //btnLaunchChat.setEnabled(true);
                        showLoading(false);
                        if (!TextUtils.isEmpty(textUserName.getText().toString())) {
                            showLoading(true);
                            cometChat.loginWithUID(getActivity(), textUserName.getText().toString(), new Callbacks() {
                                @Override
                                public void successCallback(JSONObject jsonObject) {
                                    Log.d(TAG, "Login Success : " + jsonObject.toString());
                                    Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                                    //btnLaunchChat.setEnabled(true);
                                    showLoading(false);
                                    LoggedInFragment loggedInFragment = new LoggedInFragment();
                                    FragmentManager manager = getFragmentManager();
                                    manager.beginTransaction()
                                            .replace(R.id.fragment_container, loggedInFragment,loggedInFragment.getTag())
                                            .addToBackStack(null).commit();
//                            launchChat();
                                }

                                @Override
                                public void failCallback(JSONObject jsonObject) {
                                    Log.d(TAG, "Login Fail : " + jsonObject.toString());
                                    Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                                    showLoading(false);
                                }
                            });
                            createUserInWordpress();
                        } else {
                            Toast.makeText(getActivity(), "UID be null or empty", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                catch (JSONException j){
                    j.printStackTrace();
                }

            }
            @Override
            public void failCallback(JSONObject jsonObject) {
                Log.d(TAG, "Failed to create User  : " + jsonObject.toString());
                Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                showLoading(false);

            }
        });
    }

    private void createUserInWordpress(){

        // Register Data in Wordpress

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(getActivity());
        String getnounceurl ="https://helpdessk.com/api/get_nonce/?controller=user&method=register";

        // prepare the Request
        JsonObjectRequest getNounce = new JsonObjectRequest(Request.Method.GET, getnounceurl, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        try{
                            String nonceValue = response.getString("nonce");
                            String registerUser ="https://helpdessk.com/api/user/register/?insecure=cool&username="+ textUserName.getText().toString()
                                    +"&email="+textEmail.getText().toString()
                                    +"&nonce="+ nonceValue
                                    +"&display_name="+ textFullName.getText().toString()
                                    +"&phone_number="+ textPhone.getText().toString()
                                    +"&user_pass=Password";

                            JsonObjectRequest createWPUser = new JsonObjectRequest(Request.Method.GET, registerUser, null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject userResponse) {
                                            try{
                                                String res = userResponse.getString("status");
                                                Log.d("Wordpress Response", userResponse.toString());
                                            }
                                            catch(JSONException f) {
                                                f.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("WordPess Error.Response", error.toString());
                                        }
                                    });
                            Log.d("Parsed", nonceValue);
                            queue.add(createWPUser);
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
                        Log.d("WordPr Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getNounce);

        // Register Data in Wordpress
    }

    private void showLoading(boolean show) {
        if (show) {
            pbLoading.setVisibility(View.VISIBLE);
            if(isAdded() && getActivity()!=null) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        } else {
            pbLoading.setVisibility(View.GONE);
            if(isAdded() && getActivity()!=null) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }
}
