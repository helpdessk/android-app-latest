package com.android.helpdessk.helpdesk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.helpdessk.helpdesk.R;
import com.android.helpdessk.helpdesk.fragments.HomeScreenFragment;
import com.android.helpdessk.helpdesk.fragments.LoggedInFragment;
import com.android.helpdessk.helpdesk.fragments.RegistrationFragment;
import com.inscripts.interfaces.Callbacks;
import com.inscripts.interfaces.LaunchCallbacks;
import com.inscripts.interfaces.SubscribeCallbacks;

import org.json.JSONObject;

import cometchat.inscripts.com.cometchatcore.coresdk.CometChat;
import rolebase.Home;

public class HomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment mCurrentFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        cometChat = CometChat.getInstance(HomeScreenActivity.this);
        initializeChat();

        FloatingActionButton fab = findViewById(R.id.home_launch_chat);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchChat();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        String fullname = sharedPreferences.getString("fullname","");
        String email = sharedPreferences.getString("email","");
        String phone = sharedPreferences.getString("phone","");
        Boolean loggedIn = sharedPreferences.getBoolean("loggedIn",false);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(mCurrentFragment == null){
            if(loggedIn){
                mCurrentFragment = LoggedInFragment.newInstance();
                replaceFragment(mCurrentFragment);
                Toast.makeText(HomeScreenActivity.this, user+" "+fullname+" "+email+" "+phone , Toast.LENGTH_LONG).show();
            }else{
                mCurrentFragment = RegistrationFragment.newInstance();
                replaceFragment(mCurrentFragment);
            }
        }
        replaceFragment(mCurrentFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String user = sharedPreferences.getString("username","");
        String fullname = sharedPreferences.getString("fullname","");
        String email = sharedPreferences.getString("email","");
        String phone = sharedPreferences.getString("phone","");
        Boolean loggedIn = sharedPreferences.getBoolean("loggedIn",false);


        if (id == R.id.nav_join_help_desk) {
            if(loggedIn){
                mCurrentFragment = LoggedInFragment.newInstance();
                replaceFragment(mCurrentFragment);
                Toast.makeText(HomeScreenActivity.this, user+" "+fullname+" "+email+" "+phone , Toast.LENGTH_LONG).show();
            }else{
                mCurrentFragment = RegistrationFragment.newInstance();
                replaceFragment(mCurrentFragment);
            }

        } else if (id == R.id.nav_app_help) {

        } else if (id == R.id.nav_privacy_policy) {

        } else if (id == R.id.nav_support_us) {

        }else if(id == R.id.nav_logout  ){
            cometChat.logout(new Callbacks() {
                @Override
                public void successCallback(JSONObject response) {
                    Toast.makeText(HomeScreenActivity.this, "Logged out", Toast.LENGTH_LONG).show();
                    SharedPreferences preferences = getSharedPreferences("MyData",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    finish();
                    Intent i = new Intent(HomeScreenActivity.this, LandingActivity.class);
                    startActivity(i);
                }
                @Override
                public void failCallback(JSONObject response) {
                    Toast.makeText(HomeScreenActivity.this, "Failed to Logged out", Toast.LENGTH_LONG).show();
                }
            });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment){
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment).addToBackStack(null);
            ft.commit();
        }
    }

    private void initializeChat() {

        cometChat.initializeCometChat("", licenseKey, apiKey, isCometOnDemand, new Callbacks() {
            @Override
            public void successCallback(JSONObject jsonObject) {
                Log.d(TAG, "Initialize Success : " + jsonObject.toString());
                Toast.makeText(HomeScreenActivity.this, "CometChat initialized successfully", Toast.LENGTH_LONG).show();

            }

            @Override
            public void failCallback(JSONObject jsonObject) {
                Log.d(TAG, "Initialize Fail : " + jsonObject.toString());
                Toast.makeText(HomeScreenActivity.this, "Initialize Failed with error: " + jsonObject.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void launchChat() {

        cometChat.launchCometChat(HomeScreenActivity.this, false, new LaunchCallbacks() {
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
}

