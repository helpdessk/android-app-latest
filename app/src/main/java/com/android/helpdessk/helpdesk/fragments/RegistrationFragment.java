package com.android.helpdessk.helpdesk.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.android.helpdessk.helpdesk.activities.MainActivity;
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

import org.json.JSONObject;

import cometchat.inscripts.com.cometchatcore.coresdk.CometChat;

import static android.content.Context.MODE_PRIVATE;

public class RegistrationFragment extends Fragment {

    private static final String TAG = RegistrationFragment.class.getSimpleName();

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
    private ProgressBar pbLoading;

    private TextView mTextView;

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
        //btnSetData = view.findViewById(R.id.btnSetData);
        //btnGetData = view.findViewById(R.id.btnGetData);
        btnLaunchChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUserAndLogin();
                createUserInWordpress();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username",textUserName.getText().toString());
                editor.putString("fullname",textFullName.getText().toString());
                editor.putString("email",textEmail.getText().toString());
                editor.putString("phone",textPhone.getText().toString());
                editor.putBoolean("loggedIn",true);
                editor.apply();
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
                } else {
                    Toast.makeText(getActivity(), "UID be null or empty", Toast.LENGTH_LONG).show();
                }

                // Register Data in Wordpress

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getActivity());
                String getnounceurl ="https://helpdessk.com/api/get_nonce/?controller=user&method=register";

                // prepare the Request
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getnounceurl, null,
                        new Response.Listener<JSONObject>()
                        {
                            @Override
                            public void onResponse(JSONObject response) {
                                // display response
                                JSONObject nonceValue = response;
                                Log.d("Response", response.toString());
                                String registerUser ="https://helpdessk.com/api/user/register/?insecure=cool&username="+ textUserName.getText().toString()
                                        +"&email="+textEmail.getText().toString()
                                        +"&nonce="+nonceValue.toString()
                                        +"&display_name="+ textFullName.getText().toString()
                                        +"&phone_number="+ textPhone.getText().toString()
                                        +"&user_pass=Password";

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.toString());
                            }
                        }
                );

                // add it to the RequestQueue
                queue.add(getRequest);

                // Register Data in Wordpress

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

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String getnounceurl ="https://helpdessk.com/api/get_nonce/?controller=user&method=register";

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getnounceurl, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
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

    private void login(String UID) {
        if (UID != null && !TextUtils.isEmpty(UID)) {
            showLoading(true);
            cometChat.loginWithUID(getActivity(), UID, new Callbacks() {
                @Override
                public void successCallback(JSONObject jsonObject) {
                    if (isAdded() && getActivity() != null) {
                        Log.d(TAG, "Login Success : " + jsonObject.toString());
                        Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        //btnLaunchChat.setEnabled(true);
                        showLoading(false);
                    }
                }

                @Override
                public void failCallback(JSONObject jsonObject) {
                    if (isAdded() && getActivity() != null) {
                        Log.d(TAG, "Login Fail : " + jsonObject.toString());
                        Toast.makeText(getActivity(), "Please Fill in the required fields", Toast.LENGTH_LONG).show();
                        //Toast.makeText(getActivity(), jsonObject.toString(), Toast.LENGTH_LONG).show();
                        showLoading(false);
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please Fill in the required fields", Toast.LENGTH_LONG).show();
        }

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

    public void saveData(View v){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",textUserName.getText().toString());
        editor.putString("fullname",textFullName.getText().toString());
        editor.putString("email",textEmail.getText().toString());
        editor.putString("phone",textPhone.getText().toString());
        editor.apply();
    }

    public void getData(View v){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        String fullname = sharedPreferences.getString("fullname","");
        String email = sharedPreferences.getString("email","");
        String phone = sharedPreferences.getString("phone","");

        Toast.makeText(getActivity(), user+" "+fullname+" "+email+" "+phone , Toast.LENGTH_LONG).show();

        if (isAdded() && getActivity() != null) {
            Toast.makeText(getActivity(), user+" "+fullname+" "+email+" "+phone , Toast.LENGTH_LONG).show();
        }

    }
}
