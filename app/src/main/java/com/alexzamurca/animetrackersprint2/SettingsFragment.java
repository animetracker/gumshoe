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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexzamurca.animetrackersprint2.algorithms.CancelAllAlarms;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;
import com.alexzamurca.animetrackersprint2.notifications.UpdatingDBChannel;
import com.alexzamurca.animetrackersprint2.settings.dialog_report_bug;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class SettingsFragment extends Fragment implements RewardedVideoAdListener
{
    private static final String TAG = "SettingsFragment";
    private Button darkMode;
    private NavController navController;
    private FragmentActivity mContext;
    RewardedVideoAd rewardedVideoAd;
    TextView textView;
    Button button;
    int value=0;

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

        Button store = view.findViewById(R.id.settings_store);
        store.setOnClickListener(view15 ->
        {
            navController.navigate(R.id.action_to_store);
        });

        // This method is used to create the dark mode using the button
        darkMode= view.findViewById(R.id.settings_dark_mode_button);
        darkMode.setOnClickListener(view15 ->
            {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean darkModeOn = sharedPreferences.getBoolean("dark_mode_on", false);
                if(darkModeOn)
                {
                    darkMode.setBackgroundResource(R.drawable.dark_fill_light_border);
                    String darkModeOffString = "Dark Mode: Off";
                    darkMode.setText(darkModeOffString);
                    darkMode.setTextColor(ContextCompat.getColor(requireContext(), R.color.light));
                    mContext.setTheme(R.style.AppThemeLight);
                    editor.putBoolean("dark_mode_on", false);
                }
                else
                {
                    darkMode.setBackgroundResource(R.drawable.light_fill_dark_border);
                    String darkModeOnString = "Dark Mode: On";
                    darkMode.setText(darkModeOnString);
                    darkMode.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark));
                    mContext.setTheme(R.style.AppThemeDark);
                    editor.putBoolean("dark_mode_on", true);
                }
                editor.apply();
                navController.navigate(R.id.settingsFragment);
            }
        );

        //RewardAd
        textView=view.findViewById(R.id.text_view);
        button =view.findViewById(R.id.settings_ads);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rewardedVideoAd.isLoaded())
                {
                    rewardedVideoAd.show();
                }
            }
        });

        MobileAds.initialize(mContext, "ca-app-pub-3940256099942544~3347511713");
        rewardedVideoAd= MobileAds.getRewardedVideoAdInstance(mContext);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadAds();

        return view;
    }


    //adwork
    private void loadAds() {
        rewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadAds();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        value=value+50;
        textView.setText(""+value);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

    @Override
    public void onResume() {
        rewardedVideoAd.resume(mContext);
        super.onResume();
    }

    @Override
    public void onPause() {
        rewardedVideoAd.pause(mContext);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        rewardedVideoAd.destroy(mContext);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        boolean darkModeOn = sharedPreferences.getBoolean("dark_mode_on", false);
        modifyDarkModeButton(darkModeOn);
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

    private void modifyDarkModeButton(boolean isDarkModeOn)
    {
        if(isDarkModeOn)
        {
            darkMode.setBackgroundResource(R.drawable.light_fill_dark_border);
            String darkModeOnString = "Dark Mode: On";
            darkMode.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark));
            darkMode.setText(darkModeOnString);
        }
        else
        {
            darkMode.setBackgroundResource(R.drawable.dark_fill_light_border);
            String darkModeOffString = "Dark Mode: Off";
            darkMode.setTextColor(ContextCompat.getColor(requireContext(), R.color.light));
            darkMode.setText(darkModeOffString);
        }
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
