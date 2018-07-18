package com.android.helpdessk.helpdesk.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.LaunchCallbacks;

import org.json.JSONObject;

import cometchat.inscripts.com.cometchatcore.coresdk.CometChat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String licenseKey = "COMETCHAT-0YFFT-JC76D-9GE6Y-0ZBXL";
    private String apiKey = "51202xf74431d527bc80bfcf0d142e4d5ba9f6";
    private boolean isCometOnDemand = true;
    private CometChat cometChat;
    private String avatarURL = "";
    private String profileURL = "";
    private String role = "customer";
    private Context context;

    private Button btnLaunchChat, btnInitializeChat;
    private EditText textUserName, textFullName, textEmail, textPhone;
    private ProgressBar pbLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cometChat = CometChat.getInstance(MainActivity.this);

        pbLoading = findViewById(R.id.pb_loading);
        btnLaunchChat = findViewById(R.id.btnLaunchChat);
        btnLaunchChat.setOnClickListener(this);
        btnLaunchChat.setEnabled(false);
        textUserName = findViewById(R.id.user_name);
        textFullName = findViewById(R.id.user_full_name);
        textEmail = findViewById(R.id.user_email);
        textPhone = findViewById(R.id.user_phone);

        initializeChat(); // Initialize chat on create

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLaunchChat:
                createUserAndLogin();
        }
    }

    /**
     * Initializes the Chat SDK.Initialization binds the SDK to your app and syncs the various basic parameters required for the CometChat SDK to function.
     */
    private void initializeChat() {

        if (licenseKey != null && !TextUtils.isEmpty(licenseKey)) {
            if (apiKey != null && !TextUtils.isEmpty(apiKey)) {
                showLoading(true);
                cometChat.initializeCometChat("", licenseKey, apiKey, isCometOnDemand, new Callbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {
                        //Log.d(TAG, "Initialize Success : " + jsonObject.toString());
                        Toast.makeText(MainActivity.this, "CometChat initialized successfully", Toast.LENGTH_LONG).show();
                        showLoading(false);
                        btnLaunchChat.setEnabled(true);
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {
                        Log.d(TAG, "Initialize Fail : " + jsonObject.toString());
                        Toast.makeText(MainActivity.this, "Initialize Failed with error: " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                        showLoading(false);
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "API Key cannot be null or empty", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "License Key cannot be null or empty", Toast.LENGTH_LONG).show();
        }

    }
    /**
     * Logs the user in to chat.
     *
     * @param UID - ID of the user to log in to the chat.
     *
     */
    private void login(String UID) {
        if (UID != null && !TextUtils.isEmpty(UID)) {
            showLoading(true);
            cometChat.loginWithUID(MainActivity.this, UID, new Callbacks() {
                @Override
                public void successCallback(JSONObject jsonObject) {
                    Log.d(TAG, "Login Success : " + jsonObject.toString());
                    Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    //btnLaunchChat.setEnabled(true);
                    showLoading(false);
                }

                @Override
                public void failCallback(JSONObject jsonObject) {
                    Log.d(TAG, "Login Fail : " + jsonObject.toString());
                    Toast.makeText(MainActivity.this, "Please Fill in the required fields", Toast.LENGTH_LONG).show();
                    //Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    showLoading(false);
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Please Fill in the required fields", Toast.LENGTH_LONG).show();
        }

    }

    /**
     *  Launches the chat.
     */
    private void launchChat() {

        boolean isFullScreen = true;

        cometChat.launchCometChat(MainActivity.this, true, new LaunchCallbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.d(TAG, "Launch Success : " + jsonObject.toString());
            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                Log.d(TAG, "Launch Fail : " + jsonObject.toString());
            }

            @Override
            public void userInfoCallback(JSONObject jsonObject) {
                Log.d(TAG, "User Info Received : " + jsonObject.toString());
            }

            @Override
            public void chatroomInfoCallback(JSONObject jsonObject) {
                Log.d(TAG, "Chatroom Info Received : " + jsonObject.toString());
            }

            @Override
            public void onMessageReceive(JSONObject jsonObject) {
                Log.d(TAG, "Message Received : " + jsonObject.toString());
            }

            @Override
            public void error(JSONObject jsonObject) {
                Log.d(TAG, "Error : " + jsonObject.toString());
            }

            @Override
            public void onWindowClose(JSONObject jsonObject) {
                Log.d(TAG, "Chat Window Closed : " + jsonObject.toString());
            }

            @Override
            public void onLogout() {
                Log.d(TAG, "Logout");
            }
        });

    }

    private void createUserAndLogin(){
        cometChat.createUser(context,textUserName.getText().toString(),textFullName.getText().toString(),avatarURL,profileURL,role,new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.d(TAG, "Registration Successful : " + jsonObject.toString());
                Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                //btnLaunchChat.setEnabled(true);
                showLoading(false);
                if (textUserName.getText().toString() != null && !TextUtils.isEmpty(textUserName.getText().toString())) {
                    showLoading(true);
                    cometChat.loginWithUID(MainActivity.this, textUserName.getText().toString(), new Callbacks() {
                        @Override
                        public void successCallback(JSONObject jsonObject) {
                            Log.d(TAG, "Login Success : " + jsonObject.toString());
                            Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                            //btnLaunchChat.setEnabled(true);
                            showLoading(false);
                        }

                        @Override
                        public void failCallback(JSONObject jsonObject) {
                            Log.d(TAG, "Login Fail : " + jsonObject.toString());
                            Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                            showLoading(false);
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "UID be null or empty", Toast.LENGTH_LONG).show();
                }
                cometChat.launchCometChat(MainActivity.this, true, new LaunchCallbacks() {
                    @Override
                    public void successCallback(JSONObject jsonObject) {
                        Log.d(TAG, "Launch Success : " + jsonObject.toString());
                    }

                    @Override
                    public void failCallback(JSONObject jsonObject) {
                        Log.d(TAG, "Launch Fail : " + jsonObject.toString());
                    }

                    @Override
                    public void userInfoCallback(JSONObject jsonObject) {
                        Log.d(TAG, "User Info Received : " + jsonObject.toString());
                    }

                    @Override
                    public void chatroomInfoCallback(JSONObject jsonObject) {
                        Log.d(TAG, "Chatroom Info Received : " + jsonObject.toString());
                    }

                    @Override
                    public void onMessageReceive(JSONObject jsonObject) {
                        Log.d(TAG, "Message Received : " + jsonObject.toString());
                    }

                    @Override
                    public void error(JSONObject jsonObject) {
                        Log.d(TAG, "Error : " + jsonObject.toString());
                    }

                    @Override
                    public void onWindowClose(JSONObject jsonObject) {
                        Log.d(TAG, "Chat Window Closed : " + jsonObject.toString());
                    }

                    @Override
                    public void onLogout() {
                        Log.d(TAG, "Logout");
                    }
                });
            }
            @Override
            public void failCallback(JSONObject jsonObject) {
                Log.d(TAG, "Failed to create User  : " + jsonObject.toString());
                Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                showLoading(false);

            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            pbLoading.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            pbLoading.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}

