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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

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

    private Spinner country_Spinner;
    private Spinner state_Spinner;
    private Spinner city_Spinner;

    private ArrayAdapter<Country> countryArrayAdapter;
    private ArrayAdapter<State> stateArrayAdapter;
    private ArrayAdapter<City> cityArrayAdapter;

    private ArrayList<Country> countries;
    private ArrayList<State> states;
    private ArrayList<City> cities;


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

        /*Search code*/
        country_Spinner = (Spinner) view.findViewById(R.id.country);
        state_Spinner = (Spinner) view.findViewById(R.id.state);
        city_Spinner = (Spinner) view.findViewById(R.id.category);

        countries = new ArrayList<>();
        states = new ArrayList<>();
        cities = new ArrayList<>();

        createLists();

        countryArrayAdapter = new ArrayAdapter<Country>(getActivity(), R.layout.simple_spinner_dropdown_item, countries);
        countryArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        country_Spinner.setAdapter(countryArrayAdapter);

        stateArrayAdapter = new ArrayAdapter<State>(getActivity(), R.layout.simple_spinner_dropdown_item, states);
        stateArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        state_Spinner.setAdapter(stateArrayAdapter);

        cityArrayAdapter = new ArrayAdapter<City>(getActivity(), R.layout.simple_spinner_dropdown_item, cities);
        cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        city_Spinner.setAdapter(cityArrayAdapter);

        country_Spinner.setOnItemSelectedListener(country_listener);
        state_Spinner.setOnItemSelectedListener(state_listener);
        city_Spinner.setOnItemSelectedListener(city_listener);

        /*Search code*/

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
        launchChat();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    private AdapterView.OnItemSelectedListener country_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                final Country country = (Country) country_Spinner.getItemAtPosition(position);
                Log.d("SpinnerCountry", "onItemSelected: country: "+country.getCountryID());
                ArrayList<State> tempStates = new ArrayList<>();

                tempStates.add(new State(0, new Country(0, "Choose a Country"), "Choose a State"));

                for (State singleState : states) {
                    if (singleState.getCountry().getCountryID() == country.getCountryID()) {
                        tempStates.add(singleState);
                    }
                }

                stateArrayAdapter = new ArrayAdapter<State>(getActivity(), R.layout.simple_spinner_dropdown_item, tempStates);
                stateArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                state_Spinner.setAdapter(stateArrayAdapter);
            }

            cityArrayAdapter = new ArrayAdapter<City>(getActivity(), R.layout.simple_spinner_dropdown_item, new ArrayList<City>());
            cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            city_Spinner.setAdapter(cityArrayAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener state_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                final State state = (State) state_Spinner.getItemAtPosition(position);
                Log.d("SpinnerCountry", "onItemSelected: state: "+state.getStateID());
                ArrayList<City> tempCities = new ArrayList<>();

                Country country = new Country(0, "Choose a Country");
                State firstState = new State(0, country, "Choose a State");
                tempCities.add(new City(0, country, firstState, "Choose a City"));

                for (City singleCity : cities) {
                    if (singleCity.getState().getStateID() == state.getStateID()) {
                        tempCities.add(singleCity);
                    }
                }

                cityArrayAdapter = new ArrayAdapter<City>(getActivity(), R.layout.simple_spinner_dropdown_item, tempCities);
                cityArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                city_Spinner.setAdapter(cityArrayAdapter);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener city_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void createLists() {
        Country country0 = new Country(0, "Choose a Country");
        Country country1 = new Country(1, "Country1");
        Country country2 = new Country(2, "Country2");

        countries.add(new Country(0, "Choose a Country"));
        countries.add(new Country(1, "Country1"));
        countries.add(new Country(2, "Country2"));

        State state0 = new State(0, country0, "Choose a Country");
        State state1 = new State(1, country1, "state1");
        State state2 = new State(2, country1, "state2");
        State state3 = new State(3, country2, "state3");
        State state4 = new State(4, country2, "state4");

        states.add(state0);
        states.add(state1);
        states.add(state2);
        states.add(state3);
        states.add(state4);

        cities.add(new City(0, country0, state0, "Choose a City"));
        cities.add(new City(1, country1, state1, "City1"));
        cities.add(new City(2, country1, state1, "City2"));
        cities.add(new City(3, country1, state2, "City3"));
        cities.add(new City(4, country2, state2, "City4"));
        cities.add(new City(5, country2, state3, "City5"));
        cities.add(new City(6, country2, state3, "City6"));
        cities.add(new City(7, country2, state4, "City7"));
        cities.add(new City(8, country1, state4, "City8"));
    }

    private class Country implements Comparable<Country> {

        private int countryID;
        private String countryName;


        public Country(int countryID, String countryName) {
            this.countryID = countryID;
            this.countryName = countryName;
        }

        public int getCountryID() {
            return countryID;
        }

        public String getCountryName() {
            return countryName;
        }

        @Override
        public String toString() {
            return countryName;
        }


        @Override
        public int compareTo(Country another) {
            return this.getCountryID() - another.getCountryID();//ascending order
//            return another.getCountryID()-this.getCountryID();//descending  order
        }
    }

    private class State implements Comparable<State> {

        private int stateID;
        private Country country;
        private String stateName;

        public State(int stateID, Country country, String stateName) {
            this.stateID = stateID;
            this.country = country;
            this.stateName = stateName;
        }

        public int getStateID() {
            return stateID;
        }

        public Country getCountry() {
            return country;
        }

        public String getStateName() {
            return stateName;
        }

        @Override
        public String toString() {
            return stateName;
        }

        @Override
        public int compareTo(State another) {
            return this.getStateID() - another.getStateID();//ascending order
//            return another.getStateID()-this.getStateID();//descending order
        }
    }

    private class City implements Comparable<City> {

        private int cityID;
        private Country country;
        private State state;
        private String cityName;

        public City(int cityID, Country country, State state, String cityName) {
            this.cityID = cityID;
            this.country = country;
            this.state = state;
            this.cityName = cityName;
        }

        public int getCityID() {
            return cityID;
        }

        public Country getCountry() {
            return country;
        }

        public State getState() {
            return state;
        }

        public String getCityName() {
            return cityName;
        }

        @Override
        public String toString() {
            return cityName;
        }

        @Override
        public int compareTo(City another) {
            return this.cityID - another.getCityID();//ascending order
//            return another.getCityID() - this.cityID;//descending order
        }
    }
}