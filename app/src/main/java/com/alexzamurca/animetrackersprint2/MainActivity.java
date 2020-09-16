package com.alexzamurca.animetrackersprint2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alexzamurca.animetrackersprint2.algorithms.ResetAlarmForUpdateDB;
import com.alexzamurca.animetrackersprint2.algorithms.SetAlarmsForList;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;
import com.alexzamurca.animetrackersprint2.series.HTTPRequest.GET;
import com.alexzamurca.animetrackersprint2.series.dialog.IncorrectAirDateDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NotificationsOffDialog;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.alexzamurca.animetrackersprint2.tutorial.TutorialActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    String URL = "https://gumshoe.digital15.net/series/findTitle/";
    SharedPreferences sharedPreferences;
    Boolean firstTime;
    ListFragment listFragmentInstance;

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragmentInstance  = ListFragment.getInstance();

        Intent intent = getIntent();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            ResetAlarmForUpdateDB resetAlarmForUpdateDB = new ResetAlarmForUpdateDB(this);
            resetAlarmForUpdateDB.reset();
        }

        sharedPreferences = getSharedPreferences("App", MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean("first_time", true);

        if(firstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_time", false);
            editor.apply();

            Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(tutorialIntent);
        }
        else
        {
            checkLoggedInState();
            checkIfComingFromLogin();
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        performNotificationButtonCheck();
    }

    private void initBottomNavigation()
    {
        // add a check to see if logged in or not before opening main activity
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    private void performNotificationButtonCheck()
    {
        Log.d(TAG, "performNotificationButtonCheck: is intent from MainActivity null?:" + (getIntent() != null));

        // Try get notifications off bundle
        Bundle notificationsOffBundle = getIntent().getBundleExtra("bundle_notifications_off");
        if(notificationsOffBundle!=null)
        {
            Log.d(TAG, "performNotificationButtonCheck: received a notification off bundle, meaning a notification's \"turn notifications off\" button was pressed");
            boolean notificationsOff = notificationsOffBundle.getBoolean("notifications_off");
            if(notificationsOff)
            {
                Log.d(TAG, "performNotificationButtonCheck: notifications off boolean is true");
                Series series = (Series) notificationsOffBundle.getSerializable("series");
                listFragmentInstance.OnNotificationsOffAction(series);
            }
        }

        // Try get notifications off bundle
        Bundle incorrectAirDateBundle = getIntent().getBundleExtra("bundle_incorrect_air_date");
        if(incorrectAirDateBundle!=null)
        {
            Log.d(TAG, "performNotificationButtonCheck: received a incorrect air date bundle, meaning a notification's \"incorrect air date\" button was pressed");
            boolean incorrectAirDate = incorrectAirDateBundle.getBoolean("incorrect_air_date");
            if(incorrectAirDate)
            {
                Log.d(TAG, "performNotificationButtonCheck: incorrect air date boolean is true");
                Series series = (Series) incorrectAirDateBundle.getSerializable("series");
                listFragmentInstance.OnIncorrectAirDateAction(series);
            }
        }
    }


    private void checkLoggedInState()
    {
        sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);

        boolean loggedIn = sharedPreferences.getBoolean("logged_in", false);
        if(!loggedIn)
        {
            openLoginActivity();
        }
        else
        {
            checkIfSessionExpired();
            initBottomNavigation();
        }
    }

    private void openLoginActivity()
    {
        Log.d(TAG, "openLoginActivity: started login activity from MainActivity");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void checkIfComingFromLogin()
    {
        boolean comingFromLogin = getIntent().getBooleanExtra("coming_from_login", false);
        if(comingFromLogin)
        {
            SetAlarmsForList setAlarmsForList = new SetAlarmsForList(this);
            setAlarmsForList.run();
        }
    }



    private void checkIfSessionExpired()
    {
        CheckSessionAsync checkSessionAsync = new CheckSessionAsync();
        checkSessionAsync.execute();
    }


    private class CheckSessionAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean hasSessionExpired;

        @Override
        protected Void doInBackground(Void... voids)
        {
            SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");

            GET get = new GET(URL + session + "/" + "gumshoethisisasessiontest", MainActivity.this);
            String responseString = get.sendRequest();
            // Means session is fine
            if(responseString.equals(""))
            {
                hasSessionExpired = false;
            }
            else
            {
                // try get a JSON
                try
                {
                    JSONObject response = new JSONObject(responseString);
                    hasSessionExpired = response.getBoolean("error");
                }
                catch(JSONException e)
                {
                    Log.d(TAG, "doInBackground: trying to get response from get request to check session has not returned a json (JSONException)");
                    hasSessionExpired = true;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            SharedPreferences sharedPreferences = getSharedPreferences("App", Context.MODE_PRIVATE);
            boolean connection_error = sharedPreferences.getBoolean("db_connect_problem", false);
            if(!connection_error)
            {
                if(hasSessionExpired)
                {
                    Toast.makeText(MainActivity.this, "Your session has expired, you need to re-login!", Toast.LENGTH_LONG).show();
                    openLoginActivity();
                }
            }

            super.onPostExecute(aVoid);
        }
    }
}