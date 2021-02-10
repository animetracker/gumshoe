package com.alexzamurca.animetrackersprint2.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexzamurca.animetrackersprint2.R;

import java.io.Serializable;

public class ProfileIconChangeConfirmationDialog  extends DialogFragment
{
    private static final String TAG = "ProfileIconChangeConfirmationDialog";
    private OnResponseListener onResponseListener;

    String[] cardNames = new String[]{"Default", "Ash", "Goku", "Naruto", "Luffy", "Eren"};
    int[] cardPrices = new int[]{0, 500, 500, 750, 1000, 1000};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_profile_icon_change_confirmation, container, false);
        setCancelable(false);

        Window window = requireDialog().getWindow();
        if(window!=null)
        {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        assert getArguments() != null;
        int index  = getArguments().getInt("index");
        int state  = getArguments().getInt("state");
        onResponseListener = (OnResponseListener)getArguments().getSerializable("onResponseListener");

        TextView explanation = view.findViewById(R.id.profile_icon_change_confirmation_explanation);
        String question = "";
        if(state == 1)
        {
            question = "Do you want to use the " + cardNames[index] + " Profile Icon?";
        }
        else if(state == 0)
        {
            question = "Do you want to spend " + cardPrices[index] + " Gumshoe Points to purchase the "+ cardNames[index] + " Profile Icon?";
        }
        explanation.setText(question);

        Button yesButton = view.findViewById(R.id.profile_icon_change_confirmation_yes_button);
        yesButton.setOnClickListener(v ->
        {
            yesLogic(index, state);
            dismiss();
        });

        Button noButton = view.findViewById(R.id.profile_icon_change_confirmation_no_button);
        noButton.setOnClickListener(v -> dismiss());

        ImageButton closeButton = view.findViewById(R.id.profile_icon_change_confirmation_close);
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    private void yesLogic(int index, int state)
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Profile Icons", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // if buying the icon
        if (state == 0)
        {
            // taking money away
            int points = sharedPreferences.getInt("points", 0);
            if(points >= cardPrices[index])
            {
                // changing the shared preferences of that index from 0 to 1
                editor.putInt(cardNames[index], 1);
                editor.putInt("points", points - cardPrices[index]);

                editor.apply();
            }
            else
            {
                Log.d(TAG, "yesLogic: not enough money to get this icon");
            }
        }
        // if selecting the icon
        else if (state == 1)
        {
            // for loop through shared preferences of icons, if in state 0,1, do nothing, if in state 2 then change to state 1
            for (String cardName : cardNames) {
                int temp_state = sharedPreferences.getInt(cardName, 0);
                if (temp_state == 2) {
                    editor.putInt(cardName, 1);
                    editor.apply();
                }
            }
            // change shared preferences for that index from 1 to 2
            editor.putInt(cardNames[index], 2);
            editor.apply();
        }

        onResponseListener.onYesClickListener();
    }

    public interface OnResponseListener extends Serializable {
        void onYesClickListener();
    }
}
