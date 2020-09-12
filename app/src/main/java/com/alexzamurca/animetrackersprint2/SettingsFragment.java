package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexzamurca.animetrackersprint2.algorithms.CancelAllAlarms;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;
import com.alexzamurca.animetrackersprint2.notifications.UpdatingDBChannel;
import com.alexzamurca.animetrackersprint2.settings.dialog_report_bug;
public class SettingsFragment extends Fragment
{
    private static final String TAG = "SettingsFragment";
    private SwitchCompat darkModeSwitch;
    private NavController navController;
    private FragmentActivity mContext;

    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext = (FragmentActivity)context;
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


        Button reportBug = view.findViewById(R.id.settings_report_bug);
        reportBug.setOnClickListener(view1 ->
        {
           // navController.navigate(R.id.action_reporting_bug);
            dialog_report_bug dialogReportBug = new dialog_report_bug();
            dialogReportBug.show(mContext.getSupportFragmentManager(), "dialog_report_button");
        });

        Button aboutUs = view.findViewById(R.id.settings_about);
        aboutUs.setOnClickListener(view12 ->
        {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_settingsFragment_to_aboutFragment);
        });

        Button tutorial = view.findViewById(R.id.settings_tutorial);
        tutorial.setOnClickListener(view13 -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_settingsFragment_to_tutorialActivity);
        });

        Button changeTimeZone = view.findViewById(R.id.settings_change_time_zone_button);
        changeTimeZone.setOnClickListener(view1 ->

            navController.navigate(R.id.action_change_time_zone)
        );

        Button logOut = view.findViewById(R.id.settings_logout);
        logOut.setOnClickListener(view14 ->
            openLogin()
        );

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

    public void openLogin()
    {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("logged_in", false);
        editor.apply();

        // Cancel series alarms
        CancelAllAlarms cancelAllAlarms = new CancelAllAlarms(getContext());
        cancelAllAlarms.run();

        // Cancel update DB alarm
        cancelDatabaseCheckAlarm();

        Log.d(TAG, "openLogin: opening LoginActivity from SettingsFragment - logout button pressed");
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void cancelDatabaseCheckAlarm()
    {
        UpdatingDBChannel updatingDBChannel = new UpdatingDBChannel(getContext());
        updatingDBChannel.cancel();
    }

}
