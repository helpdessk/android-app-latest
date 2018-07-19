package com.android.helpdessk.helpdesk.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.LaunchCallbacks;
import com.inscripts.interfaces.SubscribeCallbacks;

import org.json.JSONObject;

import cometchat.inscripts.com.cometchatcore.coresdk.CometChat;

public class LoggedInFragment extends Fragment {

    private static final String TAG = LoggedInFragment.class.getSimpleName();

    private String licenseKey = "COMETCHAT-0YFFT-JC76D-9GE6Y-0ZBXL";
    private String apiKey = "51202xf74431d527bc80bfcf0d142e4d5ba9f6";
    private boolean isCometOnDemand = true;
    private CometChat cometChat;
    private String avatarURL = "";
    private String profileURL = "";
    private String role = "customer";
    private Context context;

    private Button btnLaunchChat, btnInitializeChat, btnSetData, btnGetData;
    private EditText textUserName, textFullName, textEmail, textPhone;
    private TextView textLoggedUserName, textLoggedFullName, textLoggedEmail, textLoggedPhone;
    private ProgressBar pbLoading;
    
    public static LoggedInFragment newInstance(){
        return new LoggedInFragment();
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.logged_in_layout, container, false);
        cometChat = CometChat.getInstance(getActivity());
        pbLoading = view.findViewById(R.id.pb_loading);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        String fullname = sharedPreferences.getString("fullname","");
        String email = sharedPreferences.getString("email","");
        String phone = sharedPreferences.getString("phone","");


        textLoggedUserName = view.findViewById(R.id.logged_user_name);
        textLoggedFullName = view.findViewById(R.id.logged_full_name);
        textLoggedEmail = view.findViewById(R.id.logged_email);
        textLoggedPhone = view.findViewById(R.id.logged_phone);

        textLoggedUserName.setText(user);
        textLoggedFullName.setText(fullname);
        textLoggedEmail.setText(email);
        textLoggedPhone.setText(phone);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeChat();
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

    private void initializeChat() {
        showLoading(true);
        cometChat.initializeCometChat("", licenseKey, apiKey, isCometOnDemand, new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                //Log.d(TAG, "Initialize Success : " + jsonObject.toString());
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), "CometChat initialized successfully", Toast.LENGTH_LONG).show();
                    showLoading(false);
                    launchChat();
                }
            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                if (isAdded() && getActivity() != null) {
                    Log.d(TAG, "Initialize Fail : " + jsonObject.toString());
                    Toast.makeText(getActivity(), "Initialize Failed with error: " + jsonObject.toString(), Toast.LENGTH_LONG).show();
                    showLoading(false);
                }
            }
        });
    }

    /**
     *  Launches the chat.
     */
    private void launchChat() {
        boolean isFullScreen = false;
        cometChat.launchCometChat(getActivity(), isFullScreen, new LaunchCallbacks() {
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
        cometChat.subscribe(true, new SubscribeCallbacks() {
            @Override
            public void gotOnlineList(JSONObject jsonObject) {

            }

            @Override
            public void gotBotList(JSONObject jsonObject) {

            }

            @Override
            public void gotRecentChatsList(JSONObject jsonObject) {

            }

            @Override
            public void onError(JSONObject jsonObject) {

            }

            @Override
            public void onMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void gotProfileInfo(JSONObject jsonObject) {

            }

            @Override
            public void gotAnnouncement(JSONObject jsonObject) {

            }

            @Override
            public void onAVChatMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void onActionMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void onGroupMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void onGroupsError(JSONObject jsonObject) {

            }

            @Override
            public void onLeaveGroup(JSONObject jsonObject) {

            }

            @Override
            public void gotGroupList(JSONObject groupList) {

            }

            @Override
            public void gotGroupMembers(JSONObject jsonObject) {

            }

            @Override
            public void onGroupAVChatMessageReceived(JSONObject jsonObject) {

            }

            @Override
            public void onGroupActionMessageReceived(JSONObject jsonObject) {

            }
        });
    }
}
