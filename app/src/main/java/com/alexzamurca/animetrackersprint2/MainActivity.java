package com.alexzamurca.animetrackersprint2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        //By default the list fragment is shown
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListFragment(), "ListFragment").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        switch(item.getItemId())
        {
            case R.id.navigation_list:
                selectedFragment = new ListFragment();
                break;

            case R.id.navigation_profile:
                selectedFragment = new ProfileFragment();
                break;

            case R.id.navigation_settings:
                selectedFragment = new SettingsFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, selectedFragment.getClass().getName()).addToBackStack("ListFragment").commit();

        return true;
    };
}