package com.alexzamurca.animetrackersprint2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.SetAlarmsForList;
import com.alexzamurca.animetrackersprint2.algorithms.UpdateList;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.tutorial.TutorialActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    SharedPreferences sharedPreferences;
    Boolean firstTime;
    BottomNavigationView bottomNavigationView;

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBottomNavigation();
        checkIfUpdateListPending();

        Intent intent = getIntent();
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Log.d(TAG, "onCreate: reset alarm because of reboot");

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
            startActivity(tutorialIntent);
            MainActivity.this.finish();
        }

    }

    private void initBottomNavigation()
    {
        // add a check to see if logged in or not before opening main activity
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        navController = Navigation.findNavController(this, R.id.fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        checkDarkMode();
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
        if(darkModeOn)
        {
            Log.d(TAG, "checkDarkMode: dark mode is ON");
            setTheme(R.style.AppThemeDark);
            bottomNavigationView.setBackgroundResource(R.color.darkmode);
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
                    new int[] {-android.R.attr.state_checked}, // unchecked
                    new int[] { android.R.attr.state_pressed}  // pressed
            };

            int[] colors = new int[] {
                    Color.WHITE,
                    Color.RED,
                    Color.GREEN,
                    Color.BLUE
            };
            ColorStateList myList = new ColorStateList(states, colors);
            bottomNavigationView.setItemIconTintList(myList);
        }
        else
        {
            Log.d(TAG, "checkDarkMode: dark mode is OFF");
            setTheme(R.style.AppThemeLight);
            bottomNavigationView.setBackgroundResource(R.color.colorWhite);
            int[][] states = new int[][] {
                    new int[] { android.R.attr.state_enabled}, // enabled
                    new int[] {-android.R.attr.state_enabled}, // disabled
                    new int[] {-android.R.attr.state_checked}, // unchecked
                    new int[] { android.R.attr.state_pressed}  // pressed
            };

            int[] colors = new int[] {
                    Color.BLACK,
                    Color.RED,
                    Color.GREEN,
                    Color.BLUE
            };
            ColorStateList myList = new ColorStateList(states, colors);
            bottomNavigationView.setItemIconTintList(myList);
        }
    }

    private void checkIfUpdateListPending()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("App", Context.MODE_PRIVATE);
        boolean needUpdate = sharedPreferences.getBoolean("need_to_update_list", false);
        if(needUpdate)
        {
            CheckConnection checkConnection = new CheckConnection(this);
            boolean isConnected = checkConnection.isConnected();
            if(isConnected)
            {
                UpdateList updateList = new UpdateList(this);
                updateList.run();
            }
            else
            {
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_list", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(getSupportFragmentManager(), "NoConnectionDialog");
            }
        }
    }
}