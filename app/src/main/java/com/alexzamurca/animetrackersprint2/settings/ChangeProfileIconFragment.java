package com.alexzamurca.animetrackersprint2.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.dialog.NotificationsOffDialog;
import com.alexzamurca.animetrackersprint2.dialog.ProfileIconChangeConfirmationDialog;
import com.google.android.material.snackbar.Snackbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangeProfileIconFragment extends Fragment
{
    private Button[] cardButtons;
    private transient FragmentActivity mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_change_profile_icon, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext = (FragmentActivity)context;
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        cardButtons = new Button[]{view.findViewById(R.id.default_button), view.findViewById(R.id.ash_button), view.findViewById(R.id.goku_button), view.findViewById(R.id.naruto_button), view.findViewById(R.id.luffy_button), view.findViewById(R.id.eren_button)};

        Toolbar toolbar = view.findViewById(R.id.change_profile_toolbar_object);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // 0 = default, 1 = Ash (Pokemon), 2 = Goku (Dragon Ball),  3 = Naruto, 4 = Luffy (One Piece), 5 = Eren (Attack On Titan)
        initCardView();
        manageOnClicks(view);
    }

    private void initCardView()
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Profile Icons", Context.MODE_PRIVATE);
        String[] sharedPreferencesKeys = new String[]{"Default", "Ash", "Goku", "Naruto", "Luffy", "Eren"};
        String[] cardPrices = new String[]{"0 Points", "500 Points", "500 Points", "750 Points", "1000 Points", "1000 Points"};
        for (int i = 0; i < sharedPreferencesKeys.length; i++)
        {
            String key = sharedPreferencesKeys[i];
            int defaultValue = 0;
            if(i==0) defaultValue = 2;
            int state = sharedPreferences.getInt(key, defaultValue);
            // Alter button according to state
            Button button = cardButtons[i];
            switch(state)
            {
                case(0):button.setVisibility(View.VISIBLE);
                    button.setText(cardPrices[i]);
                    button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.blue_with_rounded_corners));
                    break;

                case(1):
                    button.setVisibility(View.VISIBLE);
                    button.setText("Use");
                    button.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.red_with_rounded_corners));
                    break;

                case(2):
                    button.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void manageOnClicks(View view)
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Profile Icons", Context.MODE_PRIVATE);
        String[] sharedPreferencesKeys = new String[]{"Default", "Ash", "Goku", "Naruto", "Luffy", "Eren"};
        int[] cardPrices = new int[]{0, 500, 500, 750, 1000, 1000};

        for(int j = 0; j < cardButtons.length; j++)
        {
            Button button = cardButtons[j];
            int finalJ = j;
            int defaultValue = 0;
            if(finalJ ==0) defaultValue = 2;
            int state = sharedPreferences.getInt(sharedPreferencesKeys[finalJ], defaultValue);
            int points = sharedPreferences.getInt("points", 0);
            button.setOnClickListener(v ->
            {
                // if buying and have money or in state 1
                if(((state == 0) && (points >= cardPrices[finalJ])) || (state == 1))
                {

                    ProfileIconChangeConfirmationDialog dialog = new ProfileIconChangeConfirmationDialog();
                    Bundle args = new Bundle();
                    args.putInt("index", finalJ);

                    args.putInt("state", state);
                    dialog.setArguments(args);
                    dialog.show(mContext.getSupportFragmentManager(), "ProfileIconChangeConfirmationDialog");
                }
                else if(points < cardPrices[finalJ])
                {
                    Snackbar.make(view, "You do not have enough GUMSHOE points to buy this icon!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            );
        }
    }
}
