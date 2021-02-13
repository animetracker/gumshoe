package com.alexzamurca.animetrackersprint2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.alexzamurca.animetrackersprint2.localList.LocalListStorage;
import com.alexzamurca.animetrackersprint2.dialog.ReportBugFragment;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment
{
    private Button darkMode;
    private NavController navController;
    private FragmentActivity mContext;
    RewardedAd rewardedAd;
    RewardedAd tempLoadedAd;
    TextView pointsText;
    Button adButton;

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

        CircleImageView toolbarIcon = view.findViewById(R.id.settings_profile_image);
        toolbarIcon.setOnClickListener(v->
                navController.navigate(R.id.storeFragment)
        );
        Drawable profileIcon = getProfileIcon();
        toolbarIcon.setImageDrawable(profileIcon);

        Button reportBug = view.findViewById(R.id.settings_report_bug);
        reportBug.setOnClickListener(view1 ->
        {
            ReportBugFragment dialogReportBug = new ReportBugFragment();
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

        Button clearList = view.findViewById(R.id.settings_clear_list);
        clearList.setOnClickListener(view14 ->
            clearLocalSeriesList()
        );

        Button changeProfile = view.findViewById(R.id.settings_change_profile);
        changeProfile.setOnClickListener(view15 ->
                navController.navigate(R.id.action_to_store)
        );

        Button patreonDonate = view.findViewById(R.id.settings_donate);
        patreonDonate.setOnClickListener(view16 ->
                {
                    String URL = "https://www.patreon.com/gumshoeteam";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(URL));
                    requireContext().startActivity(intent);
                }
        );

        pointsText = view.findViewById(R.id.settings_points);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Profile Icons", Context.MODE_PRIVATE);
        int points = sharedPreferences.getInt("points", 0);
        pointsText.setText("GUMSHOE Points: " + points);


        // This method is used to create the dark mode using the button
        darkMode= view.findViewById(R.id.settings_dark_mode_button);
        darkMode.setOnClickListener(view15 ->
            {
                SharedPreferences settingsSharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settingsSharedPreferences.edit();
                boolean darkModeOn = settingsSharedPreferences.getBoolean("dark_mode_on", false);
                if(darkModeOn)
                {
                    darkMode.setBackgroundResource(R.drawable.dark_fill_light_border);
                    String darkModeOffString = "Dark Mode: Off";
                    darkMode.setText(darkModeOffString);
                    darkMode.setTextColor(ContextCompat.getColor(requireContext(), R.color.light));
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
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
                    mContext.setTheme(R.style.AppThemeLight);
                    editor.putBoolean("dark_mode_on", false);
                    editor.apply();
                }
                else
                {
                    darkMode.setBackgroundResource(R.drawable.light_fill_dark_border);
                    String darkModeOnString = "Dark Mode: On";
                    darkMode.setText(darkModeOnString);
                    darkMode.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark));
                    BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
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
                    mContext.setTheme(R.style.AppThemeDark);
                    editor.putBoolean("dark_mode_on", true);
                    editor.apply();
                }

                navController.navigate(R.id.settingsFragment);
            }
        );

        //RewardAd
        MobileAds.initialize(mContext, initializationStatus -> {
        });
        rewardedAd = new RewardedAd(mContext,
                "ca-app-pub-1681443088426406/8196712694");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        pointsText = view.findViewById(R.id.settings_points);
        adButton =view.findViewById(R.id.settings_ads);

        adButton.setOnClickListener(v ->
        {
            if (rewardedAd.isLoaded())
            {
                Activity activityContext = mContext;
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onRewardedAdOpened()
                    {
                        // Ad opened.
                        tempLoadedAd = createAndLoadRewardedAd();
                    }

                    @Override
                    public void onRewardedAdClosed()
                    {
                        // Ad closed.
                        if(tempLoadedAd!=null)
                        {
                            rewardedAd = tempLoadedAd;
                            tempLoadedAd = null;
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Profile Icons", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        int points = sharedPreferences.getInt("points", 0);
                        editor.putInt("points", points + 50);
                        editor.apply();
                        pointsText.setText("GUMSHOE Points: "+ (points + 50));
                    }

                    @Override
                    public void onRewardedAdFailedToShow(AdError adError) {
                        // Ad failed to display.
                    }
                };
                rewardedAd.show(activityContext, adCallback);
            }
            else {
                Log.d("TAG", "The rewarded ad wasn't loaded yet.");
            }
        });
        return view;
    }

    private Drawable getProfileIcon()
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Profile Icons", Context.MODE_PRIVATE);
        String[] cardNames = new String[]{"Default", "Ash", "Goku", "Naruto", "Luffy", "Eren"};
        int[] profileIconsDirectories = new int[]{R.drawable.ic_profile, R.drawable.ash, R.drawable.goku, R.drawable.naruto, R.drawable.luffy, R.drawable.eren};
        for(int i = 0; i < cardNames.length; i++)
        {
            int temp_state = sharedPreferences.getInt(cardNames[i], 0);
            if(temp_state == 2)
            {
                return ContextCompat.getDrawable(requireContext(), profileIconsDirectories[i]);
            }
        }
        return ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile);
    }

    public RewardedAd createAndLoadRewardedAd() {
        RewardedAd rewardedAd = new RewardedAd(mContext,
                "ca-app-pub-3940256099942544/5224354917");
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
        return rewardedAd;
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

    void clearLocalSeriesList()
    {
        LocalListStorage localListStorage = new LocalListStorage(requireContext());
        localListStorage.store(new ArrayList<>());

        CancelAllAlarms cancelAllAlarms = new CancelAllAlarms(requireContext());
        cancelAllAlarms.run();
    }

}
