package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexzamurca.animetrackersprint2.settings.ReportBugFragment;

public class SettingsFragment extends Fragment
{
    private static final String TAG = "SettingsFragment";
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

        TextView reportBug = view.findViewById(R.id.settings_report_bug_header);
        reportBug.setOnClickListener(view1 ->
        {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_reporting_bug);
        });

        // This method is used to create the dark mode using the switch
        Switch darkModeSwitch= view.findViewById(R.id.settings_dark_mode_switch);
        darkModeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked)
            {
                // Dark mode
                Log.d(TAG, "onCheckedChanged: change to dark mode");
                //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else
            {
                // Light mode
                Log.d(TAG, "onCheckedChanged: change to light mode");
                //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.settings_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.settings_toolbar_share)
        {
            Toast.makeText(getContext(), "BugTest: share clicked!", Toast.LENGTH_LONG).show();
        }
        return true;
    }
}
