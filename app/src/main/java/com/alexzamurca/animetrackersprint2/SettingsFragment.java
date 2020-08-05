package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

        TextView reportBug = view.findViewById(R.id.settings_report_bug_header);
        reportBug.setOnClickListener(view1 ->
        {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_reporting_bug);
            /*
            ReportBugFragment reportBugFragment = new ReportBugFragment();
            final FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
            Log.d(TAG, "onCreateView: report bug clicked");
            ft.replace(R.id.fragment_container, reportBugFragment, "ReportBugFragment");
            ft.addToBackStack("SettingsFragment");
            ft.commit();
             */
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
}
