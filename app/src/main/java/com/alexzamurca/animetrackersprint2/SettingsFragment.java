package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.File;

public class SettingsFragment extends Fragment
{
    private static final String TAG = "SettingsFragment";
    private SwitchCompat darkModeSwitch;
    private NavController navController;

    String message = "Test";
    String number = "0123456789";

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Toolbar toolbar = view.findViewById(R.id.settings_toolbar_object);
        setHasOptionsMenu(true);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        TextView reportBug = view.findViewById(R.id.settings_report_bug_header);
        reportBug.setOnClickListener(view1 ->
        {
            navController.navigate(R.id.action_reporting_bug);
        });

        TextView aboutUs = view.findViewById(R.id.settings_about_header);
        aboutUs.setOnClickListener(view12 ->
        {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_settingsFragment_to_aboutFragment);
        });

        TextView tutorial = view.findViewById(R.id.settings_tutorial_header);
        tutorial.setOnClickListener(view13 -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_settingsFragment_to_tutorialActivity);
        });

        Button changeTimeZone = view.findViewById(R.id.settings_change_time_zone_button);
        changeTimeZone.setOnClickListener(view1 ->
        {
            navController.navigate(R.id.action_change_time_zone);
        });

        // This method is used to create the dark mode using the switch
        darkModeSwitch= view.findViewById(R.id.settings_dark_mode_switch);
        darkModeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) ->
        {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(isChecked)
            {
                // Dark mode
                Log.d(TAG, "onCheckedChanged: change to dark mode");
                //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                editor.putBoolean("dark_mode_on", true);

            }
            else
            {
                // Light mode
                Log.d(TAG, "onCheckedChanged: change to light mode");
                //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                editor.putBoolean("dark_mode_on", false);

            }
            editor.apply();
        });


        Spinner alertDelaySpinner = view.findViewById(R.id.settings_alert_delay_spinner);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        // By default no delay
        int alertDelayOptionIndex = sharedPreferences.getInt("global_alert_delay_option_index", 6);
        alertDelaySpinner.setSelection(alertDelayOptionIndex);

        alertDelaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: selected:" + selectionString);

                // Set index preference
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("global_alert_delay_option_index", position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean selection = sharedPreferences.getBoolean("dark_mode_on", false);
        darkModeSwitch.setChecked(selection);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.settings_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId() == R.id.settings_toolbar_share)
        {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String shareBody = "Your body here";
           // String shareSub = "Your Subject here";
           // myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity((Intent.createChooser(myIntent, "Share using")));
        }
        return true;
    }

}
