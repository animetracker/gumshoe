package com.alexzamurca.animetrackersprint2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.ResetAlarmForUpdateDB;
import com.alexzamurca.animetrackersprint2.algorithms.SetAlarmsForList;
import com.alexzamurca.animetrackersprint2.algorithms.UpdateDB;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;
import com.alexzamurca.animetrackersprint2.tutorial.TutorialActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    SharedPreferences sharedPreferences;
    Boolean firstTime;

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkDarkMode();
        checkIfUpdateDBPending();

        Intent intent = getIntent();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Log.d(TAG, "onCreate: reset alarm because of reboot");

            ResetAlarmForUpdateDB resetAlarmForUpdateDB = new ResetAlarmForUpdateDB(this);
            resetAlarmForUpdateDB.reset();

            SetAlarmsForList setAlarmsForList = new SetAlarmsForList(this);
            setAlarmsForList.run();
        }

        sharedPreferences = getSharedPreferences("App", MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean("first_time", true);

        if(firstTime)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_time", false);
            editor.apply();

            Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
            tutorialIntent.putExtra("first_time", true);
            startActivity(tutorialIntent);
            MainActivity.this.finish();
        }
        else
        {
            checkLoggedInState();
            checkIfComingFromLogin();
        }

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

    private void checkDarkMode()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean darkModeOn = sharedPreferences.getBoolean("dark_mode_on", false);
        if(darkModeOn) setTheme(R.style.AppThemeDark);
        else setTheme(R.style.AppThemeLight);
    }

    private void checkIfUpdateDBPending()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("App", Context.MODE_PRIVATE);
        boolean needDBUpdate = sharedPreferences.getBoolean("need_to_update_db", false);
        if(needDBUpdate)
        {
            CheckConnection checkConnection = new CheckConnection(this);
            boolean isConnected = checkConnection.isConnected();
            if(isConnected)
            {
                UpdateDB updateDB = new UpdateDB(this);
                updateDB.run();
            }
            else
            {
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_db", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(getSupportFragmentManager(), "NoConnectionDialog");
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
}