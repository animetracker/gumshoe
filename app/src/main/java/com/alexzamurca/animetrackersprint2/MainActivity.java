package com.alexzamurca.animetrackersprint2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{

    SharedPreferences sharedPreferences;
    Boolean firstTime;

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        firstTime = sharedPreferences.getBoolean("firstTime", true);

        if(firstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            firstTime = false;
            editor.putBoolean("firstTime", firstTime);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(intent);
        }
        else {
            // add a check to see if logged in or not before opening main activity
            checkIfOnDarkMode();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
            navController = Navigation.findNavController(this, R.id.fragment_container);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

    }

    @Override
    public boolean onSupportNavigateUp()
    {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    public void checkIfOnDarkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        Boolean darkMode = sharedPreferences.getBoolean("dark_mode_on", true);
        if(darkMode) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
            bottomNavigationView.setBackgroundResource(R.color.darkmode);
        }
        else {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
            bottomNavigationView.setBackgroundResource(R.color.whiteCardColor);
        }
    }

}